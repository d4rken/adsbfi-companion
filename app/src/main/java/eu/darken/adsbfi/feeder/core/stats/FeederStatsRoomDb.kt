package eu.darken.adsbfi.feeder.core.stats

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eu.darken.adsbfi.common.room.InstantConverter

@Database(
    entities = [
        BeastStatsEntity::class,
        MlatStatsEntity::class,
    ],
    version = 1,
    autoMigrations = [
//        AutoMigration(1, 2)
    ],
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
abstract class FeederStatsRoomDb : RoomDatabase() {
    abstract fun beastStats(): BeastStatsDao
    abstract fun mlatStats(): MlatStatsDao
}