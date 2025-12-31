package com.ebf.voyara.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// Request model for creating a trip (camelCase as per API)
@Serializable
data class CreateTripRequest(
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val coverPhoto: String? = null,
    val budget: Double? = null
)

// Response model for trip creation (snake_case as per new API schema)
@Serializable
data class CreateTripResponse(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val name: String,
    val description: String,
    @SerialName("start_date")
    val startDate: String,
    @SerialName("end_date")
    val endDate: String,
    @SerialName("cover_photo")
    val coverPhoto: String? = null,
    val budget: Double? = null,
    val status: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class Trip(
    val id: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    val name: String = "",
    @SerialName("start_date")
    val startDate: String = "",
    @SerialName("end_date")
    val endDate: String = "",
    val description: String = "",
    val stops: List<Stop> = emptyList(),
    @SerialName("cover_photo")
    val coverPhoto: String? = null,
    val budget: Double? = null,
    val status: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class Stop(
    val id: String? = null ,
    val cityName: String ,
    val arrivalDate: String ,
    val departureDate: String ,
    val activities : List<Activity> = emptyList()
)

@Serializable
data class Activity(
    val id : String? = null ,
    val title: String ,
    val cost : Double ,
    val type: String ,
    val duration: String
)

