package com.dsmp.pvpclient.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dsmp.pvpclient.domain.model.HudElement
import com.dsmp.pvpclient.domain.model.HudLayout

@Entity(tableName = "hud_layouts")
data class HudLayoutEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    /**
     * JSON-serialised list of [HudElement] via [com.dsmp.pvpclient.data.database.Converters].
     */
    @ColumnInfo(name = "elements_json")
    val elementsJson: String = "[]",

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(elements: List<HudElement>): HudLayout = HudLayout(
        id        = id,
        name      = name,
        elements  = elements,
        isActive  = isActive,
        createdAt = createdAt
    )
}

fun HudLayout.toEntity(elementsJson: String): HudLayoutEntity = HudLayoutEntity(
    id           = id,
    name         = name,
    elementsJson = elementsJson,
    isActive     = isActive,
    createdAt    = createdAt
)
