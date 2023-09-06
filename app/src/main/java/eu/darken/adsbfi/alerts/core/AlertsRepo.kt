package eu.darken.adsbfi.alerts.core

import eu.darken.adsbfi.alerts.core.api.AlertsEndpoint
import eu.darken.adsbfi.alerts.core.config.AlertSettings
import eu.darken.adsbfi.alerts.core.config.HexAlertConfig
import eu.darken.adsbfi.alerts.core.config.SquawkAlertConfig
import eu.darken.adsbfi.alerts.core.types.AircraftAlert
import eu.darken.adsbfi.alerts.core.types.HexAlert
import eu.darken.adsbfi.alerts.core.types.NewHexAlert
import eu.darken.adsbfi.alerts.core.types.SquawkAlert
import eu.darken.adsbfi.common.debug.logging.Logging.Priority.WARN
import eu.darken.adsbfi.common.debug.logging.log
import eu.darken.adsbfi.common.debug.logging.logTag
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertsRepo @Inject constructor(
    private val endpoint: AlertsEndpoint,
    private val settings: AlertSettings,
) {

    private val refreshTrigger = MutableStateFlow(UUID.randomUUID())
    val isRefreshing = MutableStateFlow(false)
    private val refreshLock = Mutex()
    private val configLock = Mutex()

    val alerts: Flow<Collection<AircraftAlert>> = combine(
        refreshTrigger,
        settings.hexAlerts.flow,
        settings.squawkAlerts.flow,
    ) { _, hexConfigs, squawkConfigs ->
        val hexInfos = endpoint.getHexAlerts(hexConfigs.configs.map { it.hexCode }.toSet())
        val squawkInfos = endpoint.getSquawkAlerts(squawkConfigs.configs.map { it.code }.toSet())

        val hexAlerts = hexConfigs.configs
            .map { config ->
                HexAlert(
                    config = config,
                    infos = hexInfos.hexes.filter { config.matches(it.hex) }.toSet()
                )
            }
        val squawkAlerts = squawkConfigs.configs
            .map { config ->
                SquawkAlert(
                    config = config,
                    infos = squawkInfos.squawks.filter { it.squawk == config.code }.toSet()
                )
            }
        hexAlerts + squawkAlerts
    }

    suspend fun refresh() {
        log(TAG) { "refresh()" } // TODO
        refreshTrigger.value = UUID.randomUUID()
        refreshLock.withLock {

        }
    }

    suspend fun addHexAlert(newAlert: NewHexAlert) {
        log(TAG) { "addHexAlert($newAlert)" }
        configLock.withLock {
            withContext(NonCancellable) {
                settings.hexAlerts.update { group ->
                    val oldConfigs = group.configs.toMutableSet()

                    val existing = group.configs.firstOrNull { it.hexCode == newAlert.hexCode }
                    if (existing != null) {
                        log(TAG, WARN) { "Replacing existing hex alert for $existing" }
                        oldConfigs.remove(existing)
                    }

                    val newConfig = HexAlertConfig(hexCode = newAlert.hexCode, label = newAlert.label)
                    group.copy(configs = oldConfigs + newConfig)
                }
            }
        }

        refresh()
    }

    suspend fun checkSquawk(squawk: SquawkCode) {
        log(TAG) { "checkSquawk($squawk)" }
        endpoint.getSquawkAlerts(setOf(squawk))
    }

    suspend fun addSquawkAlert(squawk: SquawkCode) {
        log(TAG) { "addSquawkAlert($squawk)" }

        configLock.withLock {
            withContext(NonCancellable) {
                settings.squawkAlerts.update { group ->
                    val oldConfigs = group.configs.toMutableSet()

                    val existing = group.configs.firstOrNull { it.code == squawk }
                    if (existing != null) {
                        log(TAG, WARN) { "Replacing existing alert for $squawk" }
                        oldConfigs.remove(existing)
                    }

                    group.copy(configs = oldConfigs + SquawkAlertConfig(code = squawk))
                }
            }
        }

        refresh()
    }

    suspend fun removeAlert(alert: AircraftAlert) = configLock.withLock {
        withContext(NonCancellable) {
            log(TAG) { "removeAlert($alert)" }

            when (alert) {
                is HexAlert -> settings.hexAlerts.update { group ->
                    val oldConfigs = group.configs.toMutableSet()

                    val toRemove = group.configs.firstOrNull { it.id == alert.id }
                    if (toRemove == null) log(TAG, WARN) { "Unknown feeder: $alert" }
                    else oldConfigs.remove(toRemove)

                    group.copy(configs = oldConfigs)
                }

                is SquawkAlert -> settings.hexAlerts.update { group ->
                    val oldConfigs = group.configs.toMutableSet()

                    val toRemove = group.configs.firstOrNull { it.id == alert.id }
                    if (toRemove == null) log(TAG, WARN) { "Unknown feeder: $alert" }
                    else oldConfigs.remove(toRemove)

                    group.copy(configs = oldConfigs)
                }
            }
        }

        refresh()
    }

    companion object {
        private val TAG = logTag("Alerts", "Repo")
    }
}