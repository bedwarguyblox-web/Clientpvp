package com.dsmp.pvpclient.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dsmp.pvpclient.domain.model.Statistics

@Entity(tableName = "statistics")
data class StatisticsEntity(
    /** Always 1 — singleton row pattern */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long = 1,

    @ColumnInfo(name = "sessions_played")
    val sessionsPlayed: Int = 0,

    @ColumnInfo(name = "total_usage_minutes")
    val totalUsageMinutes: Long = 0L,

    @ColumnInfo(name = "last_session_minutes")
    val lastSessionMinutes: Int = 0,

    @ColumnInfo(name = "favorite_profile_id")
    val favoriteProfileId: Long? = null,

    @ColumnInfo(name = "favorite_pack_id")
    val favoritePackId: Long? = null,

    @ColumnInfo(name = "last_launch")
    val lastLaunch: Long = 0L,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Statistics = Statistics(
        id                  = id,
        sessionsPlayed      = sessionsPlayed,
        totalUsageMinutes   = totalUsageMinutes,
        lastSessionMinutes  = lastSessionMinutes,
        favoriteProfileId   = favoriteProfileId,
        favoritePackId      = favoritePackId,
        lastLaunch          = lastLaunch,
        updatedAt           = updatedAt
    )
}

fun Statistics.toEntity(): StatisticsEntity = StatisticsEntity(
    id                 = id,
    sessionsPlayed     = sessionsPlayed,
    totalUsageMinutes  = totalUsageMinutes,
    lastSessionMinutes = lastSessionMinutes,
    favoriteProfileId  = favoriteProfileId,
    favoritePackId     = favoritePackId,
    lastLaunch         = lastLaunch,
    updatedAt          = updatedAt
)
