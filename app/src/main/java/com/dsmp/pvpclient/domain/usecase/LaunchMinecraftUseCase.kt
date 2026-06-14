package com.dsmp.pvpclient.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.dsmp.pvpclient.data.repository.ProfileRepository
import com.dsmp.pvpclient.data.repository.StatisticsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Launches Minecraft Bedrock Edition (or opens the Play Store if not installed).
 * Also records the launch event in [StatisticsRepository].
 */
class LaunchMinecraftUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val statisticsRepository: StatisticsRepository,
    private val profileRepository: ProfileRepository
) {
    companion object {
        const val MCPE_PACKAGE    = "com.mojang.minecraftpe"
        const val PLAY_STORE_URL  = "market://details?id=$MCPE_PACKAGE"
        const val PLAY_STORE_WEB  = "https://play.google.com/store/apps/details?id=$MCPE_PACKAGE"
    }

    sealed class Result {
        object Launched : Result()
        object NotInstalled : Result()
        data class Error(val message: String) : Result()
    }

    suspend operator fun invoke(): Result {
        return try {
            val pm     = context.packageManager
            val intent = pm.getLaunchIntentForPackage(MCPE_PACKAGE)

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                statisticsRepository.recordLaunch()

                // Update active-profile last-used timestamp
                profileRepository.activeProfile.collect { profile ->
                    profile?.let { profileRepository.recordUsage(it.id) }
                    return@collect            // collect first value then stop
                }

                Result.Launched
            } else {
                Result.NotInstalled
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    /** Opens the Play Store listing for Minecraft. */
    fun openPlayStore() {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (_: Exception) {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_WEB))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
