package com.example

import kotlinx.serialization.Serializable

//Пользователь
@Serializable
data class User(
    val id: Int = 0,
    val email: String,
    val name: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: User
)

@Serializable
data class Hotel(
    val id: Int = 0,
    val name: String,
    val city: String,
    val country: String,
    val description: String,
    val pricePerNight: Double,
    val rating: Double,
    val imageUrl: String? = null
)

@Serializable
data class CreateBookingRequest(
    val hotelId: Int,
    val checkIn: String,
    val checkOut: String
)

@Serializable
data class ErrorResponse(
    val message: String
)
@Serializable
data class Booking(
    val id: Int = 0,
    val userId: Int,
    val hotelId: Int,
    val hotelName: String = "",
    val hotelCity: String = "",
    val checkIn: String,
    val checkOut: String,
    val totalPrice: Double
)