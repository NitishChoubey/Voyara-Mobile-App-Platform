package com.ebf.voyara.data

import kotlinx.serialization.Serializable
import kotlinx.datetime.Clock

/**
 * Data model for trip drafts that are saved locally
 */
@Serializable
data class TripDraft(
    val id: String, // Unique ID for the draft
    val name: String,
    val destination: String = "",
    val startDate: String,
    val endDate: String,
    val description: String,
    val budget: String = "",
    val numberOfTravelers: String = "1",
    val selectedTripType: String = "Leisure",
    val coverPhotoUrl: String = "",
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)

