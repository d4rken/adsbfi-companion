package eu.darken.adsbfi.feeder.core

import eu.darken.adsbfi.feeder.core.config.FeederConfig
import eu.darken.adsbfi.feeder.core.stats.BeastStatsEntity
import eu.darken.adsbfi.feeder.core.stats.MlatStatsEntity
import java.time.Duration
import java.time.Instant

data class Feeder(
    val config: FeederConfig,
    val beastStats: BeastStatsEntity?,
    val mlatStats: MlatStatsEntity?,
) {

    val label: String
        get() = config.user ?: config.receiverId.takeLast(5)

    val isOffline: Boolean
        get() = if (config.offlineCheckTimeout != null) {
            Duration.between(lastSeen, Instant.now()) > config.offlineCheckTimeout
        } else {
            false
        }

    val lastSeen: Instant?
        get() = listOfNotNull(beastStats?.receivedAt, mlatStats?.receivedAt).maxOrNull()

    val id: ReceiverId
        get() = config.receiverId

}
