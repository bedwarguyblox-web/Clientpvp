package com.dsmp.pvpclient.domain.usecase

import android.net.Uri
import com.dsmp.pvpclient.data.repository.ResourcePackRepository
import com.dsmp.pvpclient.domain.model.PackCategory
import com.dsmp.pvpclient.domain.model.ResourcePack
import javax.inject.Inject

class ManageResourcePacksUseCase @Inject constructor(
    private val repository: ResourcePackRepository
) {
    val allPacks     get() = repository.packs
    val enabledPacks get() = repository.enabledPacks

    fun packsByCategory(category: PackCategory) = repository.packsByCategory(category)
    fun search(query: String)                   = repository.search(query)

    suspend fun enablePack(id: Long)                         = repository.setEnabled(id, true)
    suspend fun disablePack(id: Long)                        = repository.setEnabled(id, false)
    suspend fun togglePack(pack: ResourcePack)               = repository.setEnabled(pack.id, !pack.isEnabled)
    suspend fun setPriority(id: Long, priority: Int)         = repository.setPriority(id, priority)
    suspend fun deletePack(pack: ResourcePack)               = repository.delete(pack)

    suspend fun importPack(uri: Uri, name: String, category: PackCategory): Long? =
        repository.importFromUri(uri, name, category)

    suspend fun seedBuiltIns() = repository.seedBuiltInsIfEmpty()
}
