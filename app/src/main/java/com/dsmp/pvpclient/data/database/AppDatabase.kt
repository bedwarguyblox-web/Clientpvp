package com.dsmp.pvpclient.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dsmp.pvpclient.data.database.dao.HudLayoutDao
import com.dsmp.pvpclient.data.database.dao.ProfileDao
import com.dsmp.pvpclient.data.database.dao.ResourcePackDao
import com.dsmp.pvpclient.data.database.dao.StatisticsDao
import com.dsmp.pvpclient.data.database.entity.HudLayoutEntity
import com.dsmp.pvpclient.data.database.entity.ProfileEntity
import com.dsmp.pvpclient.data.database.entity.ResourcePackEntity
import com.dsmp.pvpclient.data.database.entity.StatisticsEntity

@Database(
    entities = [
        ProfileEntity::class,
        ResourcePackEntity::class,
        StatisticsEntity::class,
        HudLayoutEntity::class,
    ],
    version    = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun resourcePackDao(): ResourcePackDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun hudLayoutDao(): HudLayoutDao

    companion object {
        const val DATABASE_NAME = "dsmp_pvp_client.db"
    }
}
