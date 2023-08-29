package eu.darken.adsbfi.feeder.core

import eu.darken.adsbfi.feeder.core.config.FeederConfig
import eu.darken.adsbfi.feeder.core.stats.BeastStats
import eu.darken.adsbfi.feeder.core.stats.MlatStats
import java.time.Instant

data class Feeder(
    val config: FeederConfig,
    val beastStats: BeastStats?,
    val mlatStats: MlatStats?,
) {

    val label: String
        get() = mlatStats?.user ?: config.receiverId.takeLast(5)

    val lastSeen: Instant?
        get() = listOfNotNull(beastStats?.receivedAt, mlatStats?.receivedAt).maxOrNull()

    val id: ReceiverId
        get() = config.receiverId

}
