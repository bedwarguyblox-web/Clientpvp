package com.dsmp.pvpclient.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dsmp.pvpclient.domain.model.PerformanceMode
import com.dsmp.pvpclient.domain.model.Profile
import com.dsmp.pvpclient.domain.model.ProfileType

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,                            // ProfileType.name

    @ColumnInfo(name = "active_pack_id")
    val activePackId: Long? = null,

    @ColumnInfo(name = "performance_mode")
    val performanceMode: String = PerformanceMode.BALANCED.name,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_used")
    val lastUsed: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "icon_color_hex")
    val iconColorHex: String = "#00E676"
) {
    fun toDomain(): Profile = Profile(
        id            = id,
        name          = name,
        type          = ProfileType.fromName(type),
        activePackId  = activePackId,
        performanceMode = PerformanceMode.fromName(performanceMode),
        isActive      = isActive,
        createdAt     = createdAt,
        lastUsed      = lastUsed,
        iconColorHex  = iconColorHex
    )
}

fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    id              = id,
    name            = name,
    type            = type.name,
    activePackId    = activePackId,
    performanceMode = performanceMode.name,
    isActive        = isActive,
    createdAt       = createdAt,
    lastUsed        = lastUsed,
    iconColorHex    = iconColorHex
)
