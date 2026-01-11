package com.ebf.voyara.network

import com.ebf.voyara.data.CreateTripRequest
import com.ebf.voyara.data.CreateTripResponse
import com.ebf.voyara.data.ErrorResponse
import com.ebf.voyara.data.Trip
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class TripService {
    private val BASE_URL = "https://voyara.onrender.com"

    suspend fun createTrip(
        name: String,
        description: String,
        startDate: String,
        endDate: String,
        coverPhoto: String?,
        budget: Double?,
        authToken: String
    ): CreateTripResponse {
        try {
            val request = CreateTripRequest(
                name = name,
                description = description,
                startDate = startDate,
                endDate = endDate,
                coverPhoto = coverPhoto,
                budget = budget
            )



            val response: HttpResponse = httpClient.post("$BASE_URL/api/trips") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $authToken")
                setBody(request)
            }



            // Log response headers

            response.headers.forEach { name, values ->
                println("   $name: ${values.joinToString(", ")}")
            }

            if (!response.status.isSuccess()) {
                val errorText = response.bodyAsText()
                println("\n ERROR RESPONSE:")
                println("   Status: ${response.status.value}")
                println("   Body: $errorText")

                val errorBody = try {
                    response.body<ErrorResponse>()
                } catch (e: Exception) {
                    println("   Failed to parse error body: ${e.message}")
                    null
                }
                val errorMessage = errorBody?.message ?: errorBody?.error ?: "Failed to create trip"


                throw Exception(errorMessage)
            }

            // Log raw response for debugging
            val responseText = response.bodyAsText()
            println("\n SUCCESS RESPONSE:")
            println("   Raw JSON: $responseText")

            // Parse the response directly (flat structure)
            val parsedResponse = response.body<CreateTripResponse>()


            return parsedResponse

        } catch (e: HttpRequestTimeoutException) {

            println("Details: ${e.message}")
            e.printStackTrace()
            println("════════════════════════════════════════════════════════════\n")
            throw Exception("Connection timeout. Please check your internet connection and try again.")

        } catch (e: ClientRequestException) {
            // Handle 4xx errors (client errors like 401, 400, etc.)

            println("Status Code: ${e.response.status.value}")
            println("Status Description: ${e.response.status.description}")

            val errorBody = try {
                e.response.bodyAsText()
            } catch (_: Exception) {
                "Unable to read error body"
            }
            println("Error Body: $errorBody")

            val errorMessage = when (e.response.status.value) {
                401 -> {
                    println(" Authorization failed - Token may be invalid or expired")
                    "Unauthorized. Please login again."
                }
                400 -> {
                    println("Bad Request - Invalid data sent to server")
                    "Invalid trip data: $errorBody"
                }
                else -> {
                    println(" Client error")
                    errorBody
                }
            }
            println("════════════════════════════════════════════════════════════\n")
            throw Exception(errorMessage)

        } catch (e: ServerResponseException) {
            // Handle 5xx errors (server errors)

            println("Status Code: ${e.response.status.value}")
            println("Status Description: ${e.response.status.description}")

            val errorBody = try {
                e.response.bodyAsText()
            } catch (_: Exception) {
                "Unable to read error body"
            }

            throw Exception("Server error. Please try again later.")

        } catch (e: Exception) {
            // Re-throw if already our custom exception
            if (e.message?.contains("timeout") == true ||
                e.message?.contains("Unauthorized") == true ||
                e.message?.contains("Invalid") == true ||
                e.message?.contains("Server error") == true ||
                e.message?.contains("Failed to parse") == true) {
                throw e
            }


            println("Error Type: ${e::class.simpleName}")
            println("Error Message: ${e.message}")
            println("Stack Trace:")
            e.printStackTrace()
            println("════════════════════════════════════════════════════════════\n")
            throw Exception(e.message ?: "Failed to create trip. Please try again.")
        }
    }


    suspend fun fetchTrips(
        status: String? = "planning",
        authToken: String
    ): List<Trip> {
        try {




            println("\n AUTHORIZATION:")
            println("   Token Length: ${authToken.length} chars")
            println("   Token Preview: ${authToken.take(20)}...")

            println("\nFetching trips from backend...")

            val response: HttpResponse = httpClient.get("$BASE_URL/api/trips") {
                header("Authorization", "Bearer $authToken")
                if (status != null) {
                    parameter("status", status)
                }
            }

            println("\n RESPONSE RECEIVED:")
            println("   Status Code: ${response.status.value}")
            println("   Status Description: ${response.status.description}")
            println("   Is Success: ${response.status.isSuccess()}")

            if (!response.status.isSuccess()) {
                val errorText = try {
                    response.bodyAsText()
                } catch (e: Exception) {
                    "Unable to read error body"
                }
                println("\nERROR RESPONSE:")
                println("   Status: ${response.status.value}")
                println("   Body: $errorText")

                val errorMessage = when (response.status.value) {
                    401 -> "Unauthorized. Please login again."
                    404 -> "No trips found"
                    500 -> "Server error. Please try again later."
                    else -> "Failed to fetch trips: HTTP ${response.status.value}"
                }

                println("   Parsed Error: $errorMessage")
                println("╚════════════════════════════════════════════════════════════╝")
                throw Exception(errorMessage)
            }

            // Parse the response as list of trips - only read body once
            val trips = try {
                response.body<List<Trip>>()
            } catch (e: Exception) {
                println("\n PARSING ERROR:")
                println("   Error: ${e.message}")
                e.printStackTrace()
                throw Exception("Failed to parse trips data: ${e.message}")
            }

            println("\n SUCCESS - TRIPS PARSED:")
            println("   Total Trips: ${trips.size}")
            trips.forEachIndexed { index, trip ->
                println("\n   Trip #${index + 1}:")
                println("     ID: ${trip.id}")
                println("     Name: ${trip.name}")
                println("     Status: ${trip.status}")
                println("     Start: ${trip.startDate}")
                println("     End: ${trip.endDate}")
            }



            return trips

        } catch (e: HttpRequestTimeoutException) {

            println("Error: Connection timeout")
            e.printStackTrace()
            throw Exception("Connection timeout. Please check your internet connection.")

        } catch (e: ClientRequestException) {

            println("Status Code: ${e.response.status.value}")

            val errorMessage = when (e.response.status.value) {
                401 -> "Unauthorized. Please login again."
                404 -> "No trips found"
                else -> "Failed to fetch trips"
            }
            println("Error: $errorMessage")
            throw Exception(errorMessage)

        } catch (e: ServerResponseException) {

            println("Status Code: ${e.response.status.value}")
            val errorBody = try {
                e.response.bodyAsText()
            } catch (_: Exception) {
                "Unable to read error body"
            }
            println("Error Body: $errorBody")
            e.printStackTrace()
            throw Exception("Server error (${e.response.status.value}). Please try again later.")

        } catch (e: Exception) {
            // Check if it's already our custom exception
            if (e.message?.contains("timeout") == true ||
                e.message?.contains("Unauthorized") == true ||
                e.message?.contains("Server error") == true ||
                e.message?.contains("Failed to parse") == true ||
                e.message?.contains("Failed to fetch") == true) {
                throw e
            }


            println("Error Type: ${e::class.simpleName}")
            println("Error: ${e.message}")
            e.printStackTrace()
            throw Exception("Failed to fetch trips: ${e.message}")
        }
    }
}
