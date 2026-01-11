package com.ebf.voyara.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val httpClient = HttpClient {

    // Throw exceptions on non-2xx status codes
    expectSuccess = true

    // Configure timeouts - increased for AI service cold starts
    install(HttpTimeout) {
        requestTimeoutMillis = 90_000   // 90 seconds for the entire request (AI cold start can take time)
        connectTimeoutMillis = 30_000   // 30 seconds to establish connection
        socketTimeoutMillis = 60_000    // 60 seconds between packets (for AI processing)
    }

    // Add retry mechanism for failed requests (optimized)
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 1)  // Reduced from 2 to 1 for faster response
        exponentialDelay(base = 1.5)  // Faster exponential backoff
        retryOnException(maxRetries = 1)
        retryIf { _, response ->
            // Only retry on server errors (5xx), not client errors (4xx like 401, 400, etc.)
            response.status.value in 500..599
        }
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true // Prevents crashes if backend adds new fields
            encodeDefaults = true
        })
    }

    install(Logging) {
        level = LogLevel.ALL
    }

    // Add default request configuration
    defaultRequest {
        // You can add default headers here if needed
    }
}