package com.ebf.voyara.network

import com.ebf.voyara.data.ErrorResponse
import com.ebf.voyara.data.LoginRequest
import com.ebf.voyara.data.LoginResponse
import com.ebf.voyara.data.SignupRequest
import com.ebf.voyara.data.SignupResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AuthService {
    private val BASE_URL = "https://voyara.onrender.com"

    suspend fun login(email: String, password: String): LoginResponse {
        try {
            val response: HttpResponse = httpClient.post("$BASE_URL/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            if (!response.status.isSuccess()) {
                // Try to get error message from response
                val errorBody = try {
                    response.body<ErrorResponse>()
                } catch (e: Exception) {
                    null
                }
                val errorMessage = errorBody?.message ?: errorBody?.error ?: "Invalid credentials"
                throw Exception(errorMessage)
            }

            return response.body()
        } catch (e: HttpRequestTimeoutException) {
            throw Exception("Connection timeout. The server may be starting up. Please wait a moment and try again.")
        } catch (e: ClientRequestException) {
            // Handle 4xx errors (client errors like 401, 400, etc.)
            val errorBody = try {
                e.response.body<ErrorResponse>()
            } catch (_: Exception) {
                null
            }
            val errorMessage = errorBody?.message ?: errorBody?.error ?: "Invalid email or password"
            throw Exception(errorMessage)
        } catch (e: ServerResponseException) {
            // Handle 5xx errors (server errors)
            throw Exception("Server error. Please try again later.")
        } catch (e: Exception) {
            // Re-throw if already our custom exception
            if (e.message?.contains("Invalid") == true ||
                e.message?.contains("Connection") == true ||
                e.message?.contains("Server error") == true) {
                throw e
            }
            throw Exception(e.message ?: "Login failed. Please try again.")
        }
    }

    suspend fun signup(fullName: String, email: String, password: String): SignupResponse {
        try {
            val response: HttpResponse = httpClient.post("$BASE_URL/api/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(SignupRequest(fullName, email, password))
            }

            if (!response.status.isSuccess()) {
                // Try to get error message from response
                val errorBody = try {
                    response.body<ErrorResponse>()
                } catch (e: Exception) {
                    null
                }
                val errorMessage = errorBody?.message ?: errorBody?.error ?: "Signup failed"
                throw Exception(errorMessage)
            }

            return response.body()
        } catch (e: HttpRequestTimeoutException) {
            throw Exception("Connection timeout. The server may be starting up. Please wait a moment and try again.")
        } catch (e: ClientRequestException) {
            // Handle 4xx errors (client errors like 409 conflict, 400 bad request, etc.)
            val errorBody = try {
                e.response.body<ErrorResponse>()
            } catch (_: Exception) {
                null
            }
            val errorMessage = errorBody?.message ?: errorBody?.error ?: "Email already exists or invalid data"
            throw Exception(errorMessage)
        } catch (e: ServerResponseException) {
            // Handle 5xx errors (server errors)
            throw Exception("Server error. Please try again later.")
        } catch (e: Exception) {
            // Re-throw if already our custom exception
            if (e.message?.contains("timeout") == true ||
                e.message?.contains("already exists") == true ||
                e.message?.contains("invalid data") == true ||
                e.message?.contains("Server error") == true) {
                throw e
            }
            throw Exception(e.message ?: "Signup failed. Please try again.")
        }
    }

}
