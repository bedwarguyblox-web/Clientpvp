package com.dsmp.pvpclient.data.database

import androidx.room.TypeConverter
import com.dsmp.pvpclient.domain.model.HudElement
import com.dsmp.pvpclient.domain.model.HudElementType
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

/**
 * Flat data class used purely for Gson serialisation of [HudElement].
 * Keeps the JSON format stable even if the domain model changes.
 */
data class HudElementData(
    @SerializedName("id")         val id: String,
    @SerializedName("type")       val type: String,
    @SerializedName("x")          val x: Float,
    @SerializedName("y")          val y: Float,
    @SerializedName("scale")      val scale: Float = 1.0f,
    @SerializedName("opacity")    val opacity: Float = 1.0f,
    @SerializedName("isVisible")  val isVisible: Boolean = true
)

fun HudElement.toData() = HudElementData(id, type.name, x, y, scale, opacity, isVisible)

fun HudElementData.toDomain() = HudElement(
    id        = id,
    type      = HudElementType.entries.firstOrNull { it.name == type } ?: HudElementType.FPS_COUNTER,
    x         = x,
    y         = y,
    scale     = scale,
    opacity   = opacity,
    isVisible = isVisible
)

class Converters {
    private val gson = Gson()
    private val listType = object : TypeToken<List<HudElementData>>() {}.type

    @TypeConverter
    fun fromHudElements(elements: List<HudElementData>): String = gson.toJson(elements, listType)

    @TypeConverter
    fun toHudElements(json: String): List<HudElementData> =
        gson.fromJson<List<HudElementData>>(json, listType) ?: emptyList()
}
