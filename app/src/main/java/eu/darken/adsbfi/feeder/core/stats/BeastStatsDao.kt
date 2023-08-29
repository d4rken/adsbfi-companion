package eu.darken.adsbfi.feeder.core.stats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import eu.darken.adsbfi.feeder.core.ReceiverId
import kotlinx.coroutines.flow.Flow

@Dao
interface BeastStatsDao {
    @Query("SELECT * FROM stats_beast")
    fun getAll(): List<BeastStatsEntity>

    @Query("SELECT * FROM stats_beast ORDER BY id DESC LIMIT 1")
    fun firehose(): Flow<BeastStatsEntity?>

    @Query("SELECT * FROM stats_beast WHERE receiver_id = :receiverId ORDER BY id DESC LIMIT 1")
    fun getLatest(receiverId: ReceiverId): Flow<BeastStatsEntity?>

    @Insert
    suspend fun insert(stats: BeastStatsEntity): Long

    @Query("DELETE FROM stats_beast WHERE receiver_id = :receiverId")
    suspend fun delete(receiverId: ReceiverId): Int
}