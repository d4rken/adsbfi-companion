package eu.darken.adsbfi.alerts.core.types

import eu.darken.adsbfi.alerts.core.AircraftHex

data class NewHexAlert(
    val label: String,
    val hexCode: AircraftHex,
)