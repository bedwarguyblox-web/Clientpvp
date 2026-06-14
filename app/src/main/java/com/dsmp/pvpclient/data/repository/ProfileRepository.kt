package com.dsmp.pvpclient.data.repository

import com.dsmp.pvpclient.data.database.dao.ProfileDao
import com.dsmp.pvpclient.data.database.entity.ProfileEntity
import com.dsmp.pvpclient.data.database.entity.toEntity
import com.dsmp.pvpclient.domain.model.PerformanceMode
import com.dsmp.pvpclient.domain.model.Profile
import com.dsmp.pvpclient.domain.model.ProfileType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val dao: ProfileDao
) {
    val profiles: Flow<List<Profile>> = dao.observeAll().map { list ->
        list.map { it.toDomain() }
    }

    val activeProfile: Flow<Profile?> = dao.observeActive().map { it?.toDomain() }

    suspend fun getById(id: Long): Profile? = dao.getById(id)?.toDomain()

    suspend fun create(profile: Profile): Long = dao.insert(profile.toEntity())

    suspend fun update(profile: Profile) = dao.update(profile.toEntity())

    suspend fun delete(profile: Profile) = dao.delete(profile.toEntity())

    suspend fun setActive(id: Long) {
        dao.deactivateAll()
        dao.setActive(id)
    }

    suspend fun recordUsage(id: Long) = dao.updateLastUsed(id, System.currentTimeMillis())

    suspend fun duplicate(profile: Profile): Long {
        val copy = profile.copy(
            id        = 0,
            name      = "${profile.name} (Copy)",
            isActive  = false,
            createdAt = System.currentTimeMillis(),
            lastUsed  = System.currentTimeMillis()
        )
        return dao.insert(copy.toEntity())
    }

    /** Seeds default profiles if the database is empty. */
    suspend fun seedDefaultsIfEmpty() {
        if (dao.count() == 0) {
            val defaults = listOf(
                ProfileEntity(name = "Crystal PvP",  type = ProfileType.CRYSTAL_PVP.name, isActive = true,  iconColorHex = "#B0FFFF"),
                ProfileEntity(name = "Lifesteal",     type = ProfileType.LIFESTEAL.name,   isActive = false, iconColorHex = "#FF4444"),
                ProfileEntity(name = "Bedwars",       type = ProfileType.BEDWARS.name,     isActive = false, iconColorHex = "#4488FF"),
                ProfileEntity(name = "Practice PvP",  type = ProfileType.PRACTICE.name,    isActive = false, iconColorHex = "#FF8800",
                    performanceMode = PerformanceMode.PERFORMANCE.name),
            )
            defaults.forEach { dao.insert(it) }
        }
    }
}
