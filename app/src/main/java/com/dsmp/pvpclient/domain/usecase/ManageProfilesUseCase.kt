package com.dsmp.pvpclient.domain.usecase

import com.dsmp.pvpclient.data.repository.ProfileRepository
import com.dsmp.pvpclient.domain.model.Profile
import javax.inject.Inject

class ManageProfilesUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    val profiles     get() = repository.profiles
    val activeProfile get() = repository.activeProfile

    suspend fun createProfile(profile: Profile): Long  = repository.create(profile)
    suspend fun updateProfile(profile: Profile)        = repository.update(profile)
    suspend fun deleteProfile(profile: Profile)        = repository.delete(profile)
    suspend fun duplicateProfile(profile: Profile): Long = repository.duplicate(profile)
    suspend fun activateProfile(id: Long)              = repository.setActive(id)
    suspend fun seedDefaults()                         = repository.seedDefaultsIfEmpty()
    suspend fun getProfile(id: Long): Profile?         = repository.getById(id)
}
