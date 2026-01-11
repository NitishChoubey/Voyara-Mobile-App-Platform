package com.ebf.voyara.data

import kotlinx.serialization.Serializable


@Serializable
data class TripDraft(
    val id: String,
    val name: String,
    val destination: String = "",
    val startDate: String,
    val endDate: String,
    val description: String,
    val budget: String = "",
    val numberOfTravelers: String = "1",
    val selectedTripType: String = "Leisure",
    val coverPhotoUrl: String = "",
    val createdAt: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
)

