package eu.darken.adsbfi.alerts.core.config

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.darken.adsbfi.alerts.core.AlertId
import eu.darken.adsbfi.alerts.core.SquawkCode
import eu.darken.adsbfi.alerts.core.types.AircraftAlert

@JsonClass(generateAdapter = true)
data class SquawkAlertConfig(
    @Json(name = "transponderCode") val code: SquawkCode,
) : AircraftAlert.Config {

    override val id: AlertId
        get() = code
}