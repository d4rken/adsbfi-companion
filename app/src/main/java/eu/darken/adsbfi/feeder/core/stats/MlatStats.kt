package eu.darken.adsbfi.feeder.core.stats

import java.time.Instant

data class MlatStats(
    val receivedAt: Instant = Instant.now(),
    val user: String?,
    val messageRate: Double?,
    val latitude: Double?,
    val longitude: Double?,
    val peers: Int?,
    val outliers: Float?,
    val badSyncTimeout: Long?,
)