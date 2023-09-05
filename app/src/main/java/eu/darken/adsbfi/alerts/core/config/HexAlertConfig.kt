package eu.darken.adsbfi.alerts.core.config

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.darken.adsbfi.alerts.core.AircraftHex

@JsonClass(generateAdapter = true)
data class HexAlertConfig(
    @Json(name = "label") val label: String,
    @Json(name = "hexCode") val hexCode: AircraftHex,
) {
    fun matches(hex: String): Boolean = hex.lowercase() == hexCode.lowercase()
}