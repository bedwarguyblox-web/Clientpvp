package com.dsmp.pvpclient.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsmp.pvpclient.data.database.entity.ResourcePackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourcePackDao {

    @Query("SELECT * FROM resource_packs ORDER BY priority DESC, name ASC")
    fun observeAll(): Flow<List<ResourcePackEntity>>

    @Query("SELECT * FROM resource_packs WHERE category = :category ORDER BY priority DESC")
    fun observeByCategory(category: String): Flow<List<ResourcePackEntity>>

    @Query("SELECT * FROM resource_packs WHERE is_enabled = 1 ORDER BY priority DESC")
    fun observeEnabled(): Flow<List<ResourcePackEntity>>

    @Query("SELECT * FROM resource_packs WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<ResourcePackEntity>>

    @Query("SELECT * FROM resource_packs WHERE id = :id")
    suspend fun getById(id: Long): ResourcePackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pack: ResourcePackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(packs: List<ResourcePackEntity>)

    @Update
    suspend fun update(pack: ResourcePackEntity)

    @Delete
    suspend fun delete(pack: ResourcePackEntity)

    @Query("UPDATE resource_packs SET is_enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)

    @Query("UPDATE resource_packs SET priority = :priority WHERE id = :id")
    suspend fun setPriority(id: Long, priority: Int)

    @Query("SELECT COUNT(*) FROM resource_packs")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM resource_packs WHERE is_built_in = 1")
    suspend fun countBuiltIn(): Int
}
