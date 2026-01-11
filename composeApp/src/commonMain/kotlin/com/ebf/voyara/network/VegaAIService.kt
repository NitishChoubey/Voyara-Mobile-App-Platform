package com.ebf.voyara.network

import com.ebf.voyara.data.ErrorResponse
import com.ebf.voyara.data.VegaAIRequest
import com.ebf.voyara.data.VegaAIResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class VegaAIService {
    private val BASE_URL = "https://vegaai-auhl.onrender.com"

    suspend fun getSuggestions(request: VegaAIRequest): VegaAIResponse {
        try {
            println("🤖 VegaAI Request: Making API call to $BASE_URL/api/ai/vega/suggest")
            println("🤖 VegaAI Request Body: $request")

            val response: HttpResponse = httpClient.post("$BASE_URL/api/ai/vega/suggest") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            println("🤖 VegaAI Response Status: ${response.status}")

            if (!response.status.isSuccess()) {
                val errorBody = try {
                    response.body<ErrorResponse>()
                } catch (e: Exception) {
                    null
                }
                val errorMessage = errorBody?.message ?: errorBody?.error ?: "Failed to get suggestions"
                println("❌ VegaAI Error: $errorMessage")
                throw Exception(errorMessage)
            }

            val vegaResponse: VegaAIResponse = response.body()
            println("✅ VegaAI Success: Received ${vegaResponse.suggestions.size} suggestions")
            println("📋 VegaAI First suggestion: ${vegaResponse.suggestions.firstOrNull()}")
            println("💬 VegaAI Message: ${vegaResponse.message}")
            return vegaResponse

        } catch (e: HttpRequestTimeoutException) {
            println("❌ VegaAI Timeout Error: ${e.message}")
            throw Exception("⏱️ AI service is warming up (first time may take 60-90 seconds).\n\nThis is normal for the first request. Please try again in a moment!")
        } catch (e: ClientRequestException) {
            val errorBody = try {
                e.response.body<ErrorResponse>()
            } catch (_: Exception) {
                null
            }
            val errorMessage = errorBody?.message ?: errorBody?.error ?: "Invalid request data"
            println("❌ VegaAI Client Error: $errorMessage")
            throw Exception(errorMessage)
        } catch (e: ServerResponseException) {
            println("❌ VegaAI Server Error: ${e.message}")
            throw Exception("AI service encountered an error. Please try again in a moment.")
        } catch (e: Exception) {
            // Check if it's a socket timeout error
            if (e.message?.contains("Socket timeout", ignoreCase = true) == true ||
                e.message?.contains("timeout", ignoreCase = true) == true) {
                println("❌ VegaAI Socket Timeout: ${e.message}")
                throw Exception("⏱️ AI service is warming up (cold start).\n\nFirst request can take 60-90 seconds.\nPlease wait a moment and try again!")
            }
            if (e.message?.contains("Invalid") == true ||
                e.message?.contains("AI service") == true) {
                throw e
            }
            println("❌ VegaAI Unexpected Error: ${e.message}")
            throw Exception(e.message ?: "Failed to get AI suggestions. Please try again.")
        }
    }
}

