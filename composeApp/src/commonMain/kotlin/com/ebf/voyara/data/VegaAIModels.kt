package com.ebf.voyara.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VegaAIRequest(
    @SerialName("trip_id") val tripId: String,
    @SerialName("city") val city: String,
    @SerialName("country") val country: String,
    @SerialName("day") val day: Int,
    @SerialName("time_slot") val timeSlot: String,
    @SerialName("total_budget") val totalBudget: Double,
    @SerialName("remaining_budget") val remainingBudget: Double,
    @SerialName("preferences") val preferences: List<String>,
    @SerialName("adults") val adults: Int = 1,
    @SerialName("children") val children: Int = 0
)

@Serializable
data class VegaAIResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("trip_id") val tripId: String,
    @SerialName("city") val city: String,
    @SerialName("country") val country: String,
    @SerialName("day") val day: Int,
    @SerialName("time_slot") val timeSlot: String,
    @SerialName("adults") val adults: Int,
    @SerialName("children") val children: Int,
    @SerialName("total_budget") val totalBudget: Double,
    @SerialName("remaining_budget") val remainingBudget: Double,
    @SerialName("suggestions") val suggestions: List<Suggestion>,
    @SerialName("message") val message: String
)

@Serializable
data class Suggestion(
    @SerialName("name") val name: String = "",
    @SerialName("place_name") val placeName: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("subtitle") val subtitle: String? = null,
    @SerialName("description") val description: String = "",
    @SerialName("highlights") val highlights: List<String> = emptyList(),
    @SerialName("recommendation_reason") val recommendationReason: String? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("estimated_cost") val estimatedCost: Double? = null,
    @SerialName("cost") val cost: Double? = null,
    @SerialName("duration") val duration: String? = null,
    @SerialName("time") val time: String? = null
) {
    // Helper properties to get the actual values regardless of field name
    val displayName: String get() = name.ifBlank { placeName ?: title ?: "Unknown Place" }
    val displayCost: Double? get() = estimatedCost ?: cost
    val displayReason: String? get() = recommendationReason ?: reason
    val displayDuration: String? get() = duration ?: time
}

