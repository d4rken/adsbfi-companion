package eu.darken.adsbfi.feeder.core.stats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import eu.darken.adsbfi.feeder.core.ReceiverId
import kotlinx.coroutines.flow.Flow

@Dao
interface MlatStatsDao {
    @Query("SELECT * FROM stats_mlat")
    fun getAll(): List<MlatStatsEntity>

    @Query("SELECT * FROM stats_mlat ORDER BY id DESC LIMIT 1")
    fun firehose(): Flow<MlatStatsEntity?>

    @Query("SELECT * FROM stats_mlat WHERE receiver_id = :receiverId ORDER BY id DESC LIMIT 1")
    fun getLatest(receiverId: ReceiverId): Flow<MlatStatsEntity?>

    @Insert
    suspend fun insert(stats: MlatStatsEntity): Long

    @Query("DELETE FROM stats_mlat WHERE receiver_id = :receiverId")
    suspend fun delete(receiverId: ReceiverId): Int
}