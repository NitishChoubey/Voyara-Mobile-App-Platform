package com.ebf.voyara.data

import kotlinx.serialization.Serializable

// Signup Request
@Serializable
data class SignupRequest(
    val fullName: String,
    val email: String,
    val password: String
)

// Signup Response
@Serializable
data class SignupResponse(
    val message: String,
    val user:  UserSignup
)

@Serializable
data class UserSignup(
    val id :  String,
    val email :  String ,
    val fullName:  String ,
    val createdAt:  String? = null  // Made optional - backend might not return this
)

// Login Request
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

// Login Response
@Serializable
data class LoginResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String ,
    val user: User
)

// User Model
@Serializable
data class User(
    val id: String,
    val email: String,
    val fullName: String ,
    val createdAt: String? = null  // Made optional - backend might not return this
)

// Error Response (for API errors)
@Serializable
data class ErrorResponse(
    val message: String? = null,
    val error: String? = null
)
