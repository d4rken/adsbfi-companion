package eu.darken.adsbfi.alerts.core.config

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.darken.adsbfi.alerts.core.SquawkCode

@JsonClass(generateAdapter = true)
data class SquawkAlertConfig(
    @Json(name = "transponderCode") val code: SquawkCode,
)