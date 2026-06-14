package com.dsmp.pvpclient.data.repository

import com.dsmp.pvpclient.data.database.dao.StatisticsDao
import com.dsmp.pvpclient.data.database.entity.StatisticsEntity
import com.dsmp.pvpclient.data.database.entity.toEntity
import com.dsmp.pvpclient.domain.model.Statistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(
    private val dao: StatisticsDao
) {
    val statistics: Flow<Statistics?> = dao.observe().map { it?.toDomain() }

    suspend fun get(): Statistics? = dao.get()?.toDomain()

    /** Ensures the singleton statistics row exists. */
    suspend fun ensureExists() {
        if (dao.get() == null) {
            dao.insert(StatisticsEntity(id = 1))
        }
    }

    /** Called whenever the user taps Launch Minecraft. */
    suspend fun recordLaunch() {
        ensureExists()
        dao.recordLaunch(System.currentTimeMillis())
    }

    /**
     * Call when the user returns from Minecraft.
     * [sessionMinutes] is the elapsed game time in whole minutes.
     */
    suspend fun recordSession(sessionMinutes: Int) {
        ensureExists()
        dao.recordSession(
            sessionMinutes = sessionMinutes,
            launchTime     = System.currentTimeMillis(),
            now            = System.currentTimeMillis()
        )
    }

    suspend fun update(stats: Statistics) = dao.update(stats.toEntity())
}
