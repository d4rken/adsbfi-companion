package eu.darken.adsbfi.alerts.core.config

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.darken.adsbfi.alerts.core.AircraftHex
import eu.darken.adsbfi.alerts.core.AlertId
import eu.darken.adsbfi.alerts.core.types.AircraftAlert

@JsonClass(generateAdapter = true)
data class HexAlertConfig(
    @Json(name = "label") val label: String,
    @Json(name = "hexCode") val hexCode: AircraftHex,
) : AircraftAlert.Config {

    override val id: AlertId
        get() = hexCode

    fun matches(hex: String): Boolean = hex.lowercase() == hexCode.lowercase()
}