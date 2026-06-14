package com.dsmp.pvpclient.domain.model

/**
 * Represents a Minecraft Bedrock resource pack managed by the launcher.
 */
data class ResourcePack(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val filePath: String = "",
    val previewImagePath: String = "",
    val category: PackCategory,
    val isEnabled: Boolean = false,
    val priority: Int = 0,
    val fileSize: Long = 0L,
    val version: String = "1.0.0",
    val isBuiltIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val fileSizeFormatted: String
        get() = when {
            fileSize < 1024          -> "${fileSize}B"
            fileSize < 1024 * 1024   -> "${fileSize / 1024}KB"
            else                     -> "${"%.1f".format(fileSize / (1024.0 * 1024.0))}MB"
        }
}
