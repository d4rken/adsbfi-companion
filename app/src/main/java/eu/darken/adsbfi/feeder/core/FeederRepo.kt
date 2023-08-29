package eu.darken.adsbfi.feeder.core

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbfi.common.coroutine.AppScope
import eu.darken.adsbfi.common.debug.logging.Logging.Priority.WARN
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.debug.logging.logTag
import eu.darken.adsbfi.feeder.core.api.FeederEndpoint
import eu.darken.adsbfi.feeder.core.config.FeederConfig
import eu.darken.adsbfi.feeder.core.config.FeederSettings
import eu.darken.adsbfi.feeder.core.stats.BeastStats
import eu.darken.adsbfi.feeder.core.stats.MlatStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeederRepo @Inject constructor(
    @ApplicationContext private val context: Context,
    @AppScope private val appScope: CoroutineScope,
    private val moshi: Moshi,
    private val feederSettings: FeederSettings,
    private val feederEndpoint: FeederEndpoint,
) {

    private val refreshTrigger = MutableStateFlow(UUID.randomUUID())

    private val configLock = Mutex()

    val feeders: Flow<Collection<Feeder>> = combine(
        refreshTrigger,
        feederSettings.feederGroup.flow,
    ) { _, group ->
        val stats = feederEndpoint.getFeeder(group.configs.map { it.receiverId })
        group.configs.map { config ->
            val mlat = stats.mlatInfos.singleOrNull { it.uuid == config.receiverId }
            val beast = stats.beastInfos.singleOrNull { it.receiverId == config.receiverId }
            Feeder(
                config = config,
                beastStats = BeastStats(
                    bandwidth = beast?.bandwidth,
                    positions = beast?.positions,
                    positionRate = beast?.positionRate,
                    messageRate = beast?.messageRate,
                    latency = beast?.recentRtt,
                ),
                mlatStats = MlatStats(
                    user = mlat?.user,
                    messageRate = mlat?.messageRate,
                    latitude = mlat?.lat,
                    longitude = mlat?.lon,
                    peers = mlat?.peerCount,
                    outliers = mlat?.outlierPercent,
                    badSyncTimeout = mlat?.badSyncTimeout,
                )
            )
        }
    }

    suspend fun refresh() {
        log(TAG) { "refresh()" }
        refreshTrigger.value = UUID.randomUUID()
    }

    suspend fun addFeeder(id: ReceiverId) = configLock.withLock {
        log(TAG) { "addFeeder($id)" }
        feederSettings.feederGroup.update { group ->
            val oldConfigs = group.configs.toMutableSet()

            val existing = group.configs.firstOrNull { it.receiverId == id }
            if (existing != null) {
                log(TAG, WARN) { "Replacing existing feeder with this ID: $existing" }
                oldConfigs.remove(existing)
            }

            group.copy(configs = oldConfigs + FeederConfig(receiverId = id))
        }
    }

    suspend fun removeFeeder(id: ReceiverId) = configLock.withLock {
        log(TAG) { "removeFeeder($id)" }
        feederSettings.feederGroup.update { group ->
            val oldConfigs = group.configs.toMutableSet()

            val toRemove = group.configs.firstOrNull { it.receiverId == id }
            if (toRemove == null) log(TAG, WARN) { "Unknown feeder: $id" }
            else oldConfigs.remove(toRemove)

            group.copy(configs = oldConfigs)
        }
    }

    fun setOfflineCheckTimeout(id: ReceiverId, timeout: Duration?) {
        log(TAG) { "setOfflineCheckTimeout($id,$timeout)" }
    }

    companion object {
        private val TAG = logTag("Feeder", "Repo")
    }
}

