package eu.darken.adsbfi.alerts.core.types

import eu.darken.adsbfi.alerts.core.AircraftHex
import eu.darken.adsbfi.alerts.core.api.AlertsApi
import eu.darken.adsbfi.alerts.core.config.HexAlertConfig

data class HexAlert(
    val config: HexAlertConfig,
    val infos: Set<AlertsApi.Alerts.Hex>,
) : AircraftAlert {
    val hex: AircraftHex
        get() = config.hexCode

    override val id: String
        get() = hex

    val label: String
        get() = config.label
}
