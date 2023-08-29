package eu.darken.adsbfi.feeder.core.stats

import java.time.Instant

data class BeastStats(
    val receivedAt: Instant = Instant.now(),
    val bandwidth: Double?,
    val positions: Long?,
    val positionRate: Double?,
    val messageRate: Double?,
//    val latency: Long?,
)