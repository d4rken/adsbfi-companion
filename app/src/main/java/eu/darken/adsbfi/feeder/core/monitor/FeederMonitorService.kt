package eu.darken.adsbfi.feeder.core.monitor

import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import eu.darken.adsbfi.common.coroutine.AppScope
import eu.darken.adsbfi.common.debug.logging.Logging.Priority.ERROR
import eu.darken.adsbfi.common.debug.logging.Logging.Priority.VERBOSE
import eu.darken.adsbfi.common.debug.logging.asLog
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.debug.logging.logTag
import eu.darken.adsbfi.feeder.core.FeederRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FeederMonitorService @Inject constructor(
    @AppScope private val appScope: CoroutineScope,
    private val workManager: WorkManager,
    private val networkStatsRepo: FeederRepo,
) {

    private var isInit = false
    fun setup() {
        log(TAG) { "setup()" }
        require(!isInit)
        isInit = true

        runBlocking { setupPeriodicWorker() }

        appScope.launch {
            try {
                networkStatsRepo.refresh()
            } catch (e: Exception) {
                log(TAG, ERROR) { "Failed to refresh: ${e.asLog()}" }
            }
        }
    }

    private suspend fun setupPeriodicWorker() {
        val workRequest = PeriodicWorkRequestBuilder<FeederMonitorWorker>(
            Duration.ofHours(1),
            Duration.ofMinutes(10)
        ).apply {
            setInputData(Data.Builder().build())
        }.build()

        log(TAG, VERBOSE) { "Worker request: $workRequest" }

        val operation = workManager.enqueueUniquePeriodicWork(
            "feeder.monitor.worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )

        operation.await()
    }

    companion object {
        val TAG = logTag("Feeder", "Monitor", "Service")
    }
}