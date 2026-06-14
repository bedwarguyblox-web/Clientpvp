package com.dsmp.pvpclient.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsmp.pvpclient.data.database.entity.HudLayoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HudLayoutDao {

    @Query("SELECT * FROM hud_layouts ORDER BY created_at DESC")
    fun observeAll(): Flow<List<HudLayoutEntity>>

    @Query("SELECT * FROM hud_layouts WHERE is_active = 1 LIMIT 1")
    fun observeActive(): Flow<HudLayoutEntity?>

    @Query("SELECT * FROM hud_layouts WHERE id = :id")
    suspend fun getById(id: Long): HudLayoutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(layout: HudLayoutEntity): Long

    @Update
    suspend fun update(layout: HudLayoutEntity)

    @Delete
    suspend fun delete(layout: HudLayoutEntity)

    @Query("UPDATE hud_layouts SET is_active = 0")
    suspend fun deactivateAll()

    @Query("UPDATE hud_layouts SET is_active = 1 WHERE id = :id")
    suspend fun setActive(id: Long)

    @Query("SELECT COUNT(*) FROM hud_layouts")
    suspend fun count(): Int
}
