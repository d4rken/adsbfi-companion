package eu.darken.adsbfi.alerts.core.types

import eu.darken.adsbfi.alerts.core.SquawkCode
import eu.darken.adsbfi.alerts.core.api.AlertsApi
import eu.darken.adsbfi.alerts.core.config.SquawkAlertConfig

data class SquawkAlert(
    val config: SquawkAlertConfig,
    val infos: Set<AlertsApi.Alerts.Squawk>,
) : AircraftAlert {
    override val id: String
        get() = squawk

    val squawk: SquawkCode
        get() = config.code
}
