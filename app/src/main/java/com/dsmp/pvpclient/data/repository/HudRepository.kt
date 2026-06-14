package com.dsmp.pvpclient.data.repository

import com.dsmp.pvpclient.data.database.Converters
import com.dsmp.pvpclient.data.database.dao.HudLayoutDao
import com.dsmp.pvpclient.data.database.entity.HudLayoutEntity
import com.dsmp.pvpclient.data.database.toData
import com.dsmp.pvpclient.data.database.toDomain
import com.dsmp.pvpclient.domain.model.HudLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HudRepository @Inject constructor(
    private val dao: HudLayoutDao,
    private val converters: Converters
) {
    val layouts: Flow<List<HudLayout>> = dao.observeAll().map { entities ->
        entities.map { it.deserialize() }
    }

    val activeLayout: Flow<HudLayout?> = dao.observeActive().map { it?.deserialize() }

    suspend fun getById(id: Long): HudLayout? = dao.getById(id)?.deserialize()

    suspend fun save(layout: HudLayout): Long {
        val json   = converters.fromHudElements(layout.elements.map { it.toData() })
        val entity = HudLayoutEntity(
            id           = layout.id,
            name         = layout.name,
            elementsJson = json,
            isActive     = layout.isActive,
            createdAt    = layout.createdAt
        )
        return dao.insert(entity)
    }

    suspend fun update(layout: HudLayout) {
        val json   = converters.fromHudElements(layout.elements.map { it.toData() })
        val entity = HudLayoutEntity(
            id           = layout.id,
            name         = layout.name,
            elementsJson = json,
            isActive     = layout.isActive,
            createdAt    = layout.createdAt
        )
        dao.update(entity)
    }

    suspend fun delete(layout: HudLayout) = dao.delete(
        HudLayoutEntity(id = layout.id, name = layout.name)
    )

    suspend fun setActive(id: Long) {
        dao.deactivateAll()
        dao.setActive(id)
    }

    /** Seeds the default HUD layout on first run. */
    suspend fun seedDefaultIfEmpty() {
        if (dao.count() == 0) {
            val id = save(HudLayout.default())
            dao.setActive(id)
        }
    }

    private fun HudLayoutEntity.deserialize(): HudLayout {
        val elements = converters.toHudElements(elementsJson).map { it.toDomain() }
        return toDomain(elements)
    }
}
