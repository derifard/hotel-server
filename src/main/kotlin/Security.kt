package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

private const val JWT_SECRET = "my-super-secret-key-for-hotel-app-2024"
private const val JWT_ISSUER = "hotel-app"
private const val JWT_AUDIENCE = "hotel-app-users"
private const val TOKEN_EXPIRATION_MS = 86400000L // 24 часа

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "hotel app"
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withAudience(JWT_AUDIENCE)
                    .withIssuer(JWT_ISSUER)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                if (userId != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

fun generateToken(userId: Int, email: String): String {
    return JWT.create()
        .withAudience(JWT_AUDIENCE)
        .withIssuer(JWT_ISSUER)
        .withClaim("userId", userId)
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
        .sign(Algorithm.HMAC256(JWT_SECRET))
}