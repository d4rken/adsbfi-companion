package eu.darken.adsbfi.feeder.core.stats

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import eu.darken.adsbfi.feeder.core.ReceiverId
import java.time.Instant

@Entity(tableName = "stats_mlat")
data class MlatStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "receiver_id") val receiverId: ReceiverId,
    @ColumnInfo(name = "received_at") val receivedAt: Instant = Instant.now(),
    @ColumnInfo(name = "message_rate") val messageRate: Double,
    @ColumnInfo(name = "peer_count") val peerCount: Int,
    @ColumnInfo(name = "badsync_timeout") val badSyncTimeout: Long,
    @ColumnInfo(name = "outlier_percent") val outlierPercent: Float,
)