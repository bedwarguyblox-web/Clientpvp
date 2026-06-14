package com.dsmp.pvpclient.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dsmp.pvpclient.domain.model.PackCategory
import com.dsmp.pvpclient.domain.model.ResourcePack

@Entity(tableName = "resource_packs")
data class ResourcePackEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "file_path")
    val filePath: String = "",

    @ColumnInfo(name = "preview_image_path")
    val previewImagePath: String = "",

    @ColumnInfo(name = "category")
    val category: String,                       // PackCategory.name

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = false,

    @ColumnInfo(name = "priority")
    val priority: Int = 0,

    @ColumnInfo(name = "file_size")
    val fileSize: Long = 0L,

    @ColumnInfo(name = "version")
    val version: String = "1.0.0",

    @ColumnInfo(name = "is_built_in")
    val isBuiltIn: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): ResourcePack = ResourcePack(
        id               = id,
        name             = name,
        description      = description,
        filePath         = filePath,
        previewImagePath = previewImagePath,
        category         = PackCategory.fromName(category),
        isEnabled        = isEnabled,
        priority         = priority,
        fileSize         = fileSize,
        version          = version,
        isBuiltIn        = isBuiltIn,
        createdAt        = createdAt
    )
}

fun ResourcePack.toEntity(): ResourcePackEntity = ResourcePackEntity(
    id               = id,
    name             = name,
    description      = description,
    filePath         = filePath,
    previewImagePath = previewImagePath,
    category         = category.name,
    isEnabled        = isEnabled,
    priority         = priority,
    fileSize         = fileSize,
    version          = version,
    isBuiltIn        = isBuiltIn,
    createdAt        = createdAt
)
