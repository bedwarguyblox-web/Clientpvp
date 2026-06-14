package com.dsmp.pvpclient.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsmp.pvpclient.data.database.entity.StatisticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {

    @Query("SELECT * FROM statistics WHERE id = 1")
    fun observe(): Flow<StatisticsEntity?>

    @Query("SELECT * FROM statistics WHERE id = 1")
    suspend fun get(): StatisticsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: StatisticsEntity)

    @Update
    suspend fun update(stats: StatisticsEntity)

    @Query("""
        UPDATE statistics 
        SET sessions_played      = sessions_played + 1,
            total_usage_minutes  = total_usage_minutes + :sessionMinutes,
            last_session_minutes = :sessionMinutes,
            last_launch          = :launchTime,
            updated_at           = :now
        WHERE id = 1
    """)
    suspend fun recordSession(sessionMinutes: Int, launchTime: Long, now: Long)

    @Query("UPDATE statistics SET last_launch = :time, updated_at = :time WHERE id = 1")
    suspend fun recordLaunch(time: Long)
}
