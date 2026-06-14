package com.dsmp.pvpclient.data.repository

import android.content.Context
import android.net.Uri
import com.dsmp.pvpclient.data.database.dao.ResourcePackDao
import com.dsmp.pvpclient.data.database.entity.ResourcePackEntity
import com.dsmp.pvpclient.data.database.entity.toEntity
import com.dsmp.pvpclient.domain.model.PackCategory
import com.dsmp.pvpclient.domain.model.ResourcePack
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourcePackRepository @Inject constructor(
    private val dao: ResourcePackDao,
    @ApplicationContext private val context: Context
) {
    val packs: Flow<List<ResourcePack>> = dao.observeAll().map { list ->
        list.map { it.toDomain() }
    }

    val enabledPacks: Flow<List<ResourcePack>> = dao.observeEnabled().map { list ->
        list.map { it.toDomain() }
    }

    fun packsByCategory(category: PackCategory): Flow<List<ResourcePack>> =
        dao.observeByCategory(category.name).map { list -> list.map { it.toDomain() } }

    fun search(query: String): Flow<List<ResourcePack>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: Long): ResourcePack? = dao.getById(id)?.toDomain()

    suspend fun insert(pack: ResourcePack): Long = dao.insert(pack.toEntity())

    suspend fun update(pack: ResourcePack) = dao.update(pack.toEntity())

    suspend fun delete(pack: ResourcePack) {
        // Remove the file from internal storage if it was imported
        if (pack.filePath.isNotEmpty() && !pack.isBuiltIn) {
            File(pack.filePath).takeIf { it.exists() }?.delete()
        }
        dao.delete(pack.toEntity())
    }

    suspend fun setEnabled(id: Long, enabled: Boolean) = dao.setEnabled(id, enabled)

    suspend fun setPriority(id: Long, priority: Int) = dao.setPriority(id, priority)

    /**
     * Copies an imported .mcpack / .zip file to internal storage and creates a DB entry.
     * Returns the id of the new pack, or null on failure.
     */
    suspend fun importFromUri(uri: Uri, packName: String, category: PackCategory): Long? =
        withContext(Dispatchers.IO) {
            try {
                val destDir = File(context.filesDir, "resource_packs").also { it.mkdirs() }
                val ext     = context.contentResolver.getType(uri)?.let {
                    if (it.contains("zip")) "zip" else "mcpack"
                } ?: "mcpack"
                val destFile = File(destDir, "${packName.replace(" ", "_")}_${System.currentTimeMillis()}.$ext")

                context.contentResolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output -> input.copyTo(output) }
                }

                val size = destFile.length()
                val pack = ResourcePackEntity(
                    name      = packName,
                    filePath  = destFile.absolutePath,
                    category  = category.name,
                    fileSize  = size,
                    isBuiltIn = false
                )
                dao.insert(pack)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    /** Seeds the built-in PvP pack catalogue if it hasn't been done yet. */
    suspend fun seedBuiltInsIfEmpty() {
        if (dao.countBuiltIn() > 0) return

        val builtIns = listOf(
            // ── Short Swords ───────────────────────────────────────────────
            ResourcePackEntity(name = "Short Swords 16x",      category = PackCategory.SHORT_SWORDS.name, isBuiltIn = true, version = "1.0.0", description = "Classic 16x short swords for clean hit detection."),
            ResourcePackEntity(name = "Short Swords 32x",      category = PackCategory.SHORT_SWORDS.name, isBuiltIn = true, version = "1.0.0", description = "Crisp 32x short swords with minimal visual noise."),
            ResourcePackEntity(name = "Short Swords 64x",      category = PackCategory.SHORT_SWORDS.name, isBuiltIn = true, version = "1.0.0", description = "High-res 64x short swords for premium visual clarity."),

            // ── Low Fire ───────────────────────────────────────────────────
            ResourcePackEntity(name = "Ultra Low Fire",        category = PackCategory.LOW_FIRE.name,     isBuiltIn = true, version = "1.0.0", description = "Barely-visible fire overlay — maximum visibility."),
            ResourcePackEntity(name = "Low Fire",              category = PackCategory.LOW_FIRE.name,     isBuiltIn = true, version = "1.0.0", description = "Reduced-height fire for better in-game visibility."),
            ResourcePackEntity(name = "Transparent Fire",      category = PackCategory.LOW_FIRE.name,     isBuiltIn = true, version = "1.0.0", description = "Fully transparent fire — see through the flames."),

            // ── Clean Blocks ───────────────────────────────────────────────
            ResourcePackEntity(name = "Cleaner Wool",          category = PackCategory.PVP.name,          isBuiltIn = true, version = "1.0.0", description = "Removes distracting wool texture noise."),
            ResourcePackEntity(name = "Cleaner Ores",          category = PackCategory.PVP.name,          isBuiltIn = true, version = "1.0.0", description = "Flat-tone ores for faster recognition."),
            ResourcePackEntity(name = "Cleaner Obsidian",      category = PackCategory.PVP.name,          isBuiltIn = true, version = "1.0.0", description = "Solid obsidian — no reflective noise."),
            ResourcePackEntity(name = "Cleaner Stone",         category = PackCategory.PVP.name,          isBuiltIn = true, version = "1.0.0", description = "Unified stone blocks for better readability."),
            ResourcePackEntity(name = "Cleaner Glass",         category = PackCategory.PVP.name,          isBuiltIn = true, version = "1.0.0", description = "Nearly-transparent glass panes."),

            // ── Visibility ─────────────────────────────────────────────────
            ResourcePackEntity(name = "Clear Water",           category = PackCategory.FPS.name,           isBuiltIn = true, version = "1.0.0", description = "Crystal-clear water — spot players underwater."),
            ResourcePackEntity(name = "Reduced Particles",     category = PackCategory.FPS.name,           isBuiltIn = true, version = "1.0.0", description = "Removes most particles for better FPS."),
            ResourcePackEntity(name = "Clean Skies",           category = PackCategory.SKY.name,           isBuiltIn = true, version = "1.0.0", description = "Solid-colour sky — no cloud or sun distractions."),
        )

        dao.insertAll(builtIns)
    }
}
