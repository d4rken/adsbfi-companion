package eu.darken.adsbfi.feeder.core.config

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import eu.darken.adsbfi.feeder.core.ReceiverId

@JsonClass(generateAdapter = true)
data class FeederConfig(
    @Json(name = "receiverId") val receiverId: ReceiverId,
    @Json(name = "anywhereId") val anywhereId: String? = null,
)