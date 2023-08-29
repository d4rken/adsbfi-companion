package eu.darken.adsbfi.feeder.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbfi.common.coroutine.AppScope
import eu.darken.adsbfi.common.coroutine.DispatcherProvider
import eu.darken.adsbfi.common.debug.logging.Logging.Priority.INFO
import eu.darken.adsbfi.common.debug.logging.Logging.Priority.WARN
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.debug.logging.logTag
import eu.darken.adsbfi.feeder.core.api.FeederEndpoint
import eu.darken.adsbfi.feeder.core.config.FeederConfig
import eu.darken.adsbfi.feeder.core.config.FeederSettings
import eu.darken.adsbfi.feeder.core.stats.BeastStatsEntity
import eu.darken.adsbfi.feeder.core.stats.FeederStatsDatabase
import eu.darken.adsbfi.feeder.core.stats.MlatStatsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Duration
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeederRepo @Inject constructor(
    @ApplicationContext private val context: Context,
    @AppScope private val appScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider,
    private val feederSettings: FeederSettings,
    private val feederEndpoint: FeederEndpoint,
    private val feederStatsDatabase: FeederStatsDatabase,
) {

    private val refreshTrigger = MutableStateFlow(UUID.randomUUID())
    val isRefreshing = MutableStateFlow(false)
    private val refreshLock = Mutex()
    private val configLock = Mutex()

    val feeders: Flow<Collection<Feeder>> = combine(
        refreshTrigger,
        feederStatsDatabase.beastStats.firehose(),
        feederStatsDatabase.mlatStats.firehose(),
        feederSettings.feederGroup.flow,
    ) { _, latestBeast, latestMlat, configGroup ->

        configGroup.configs.map { config ->
            val beastLastDb = feederStatsDatabase.beastStats.getLatest(config.receiverId).firstOrNull()
            val mlatLastDb = feederStatsDatabase.mlatStats.getLatest(config.receiverId).firstOrNull()
            Feeder(
                config = config,
                beastStats = beastLastDb,
                mlatStats = mlatLastDb,
            )
        }
    }

    suspend fun refresh() {
        log(TAG) { "refresh()" }
        val idsToRefresh = feeders.first().map { it.id }
        refreshStatsFor(idsToRefresh)
    }

    suspend fun addFeeder(id: ReceiverId) {
        configLock.withLock {
            withContext(NonCancellable) {
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
        }

        refreshStatsFor(setOf(id))
    }

    private suspend fun refreshStatsFor(ids: Collection<ReceiverId>) = refreshLock.withLock {
        log(TAG) { "refreshStatsfor($ids)" }

        if (isRefreshing.value) return@withLock
        isRefreshing.value = true

        try {
            val stats = feederEndpoint.getFeeder(ids.toSet())

            stats.beastInfos
                .map {
                    BeastStatsEntity(
                        receiverId = it.receiverId,
                        positionRate = it.positionRate,
                        positions = it.positions,
                        messageRate = it.messageRate,
                        bandwidth = it.bandwidth,
                        connectionTime = it.connTime,
                        latency = 100,
                    )
                }
                .forEach {
                    log(TAG) { "Updating beast stats : $it" }
                    feederStatsDatabase.beastStats.insert(it)
                }

            stats.mlatInfos
                .map {
                    MlatStatsEntity(
                        receiverId = it.uuid,
                        messageRate = it.messageRate,
                        peerCount = it.peerCount,
                        badSyncTimeout = it.badSyncTimeout,
                        outlierPercent = it.outlierPercent,
                    )
                }
                .forEach {
                    log(TAG) { "Updating mlat stats : $it" }
                    feederStatsDatabase.mlatStats.insert(it)
                }
        } finally {
            isRefreshing.value = false
        }
    }

    suspend fun removeFeeder(id: ReceiverId) = configLock.withLock {
        withContext(NonCancellable) {
            log(TAG) { "removeFeeder($id)" }
            feederSettings.feederGroup.update { group ->
                val oldConfigs = group.configs.toMutableSet()

                val toRemove = group.configs.firstOrNull { it.receiverId == id }
                if (toRemove == null) log(TAG, WARN) { "Unknown feeder: $id" }
                else oldConfigs.remove(toRemove)

                group.copy(configs = oldConfigs)
            }

            feederStatsDatabase.apply {
                beastStats.delete(id).also {
                    log(TAG, INFO) { "Delete $it beast stats rows" }
                }
                mlatStats.delete(id).also {
                    log(TAG, INFO) { "Delete $it mlat stats rows" }
                }
            }
        }

    }

    fun setOfflineCheckTimeout(id: ReceiverId, timeout: Duration?) {
        log(TAG) { "setOfflineCheckTimeout($id,$timeout)" }
    }

    companion object {
        private val TAG = logTag("Feeder", "Repo")
    }
}

