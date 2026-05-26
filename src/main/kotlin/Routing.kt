package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    HotelsService.seedHotels()

    routing {
        get("/") {
            call.respondText("Hotel API is running!")
        }

        post("/register") {
            val request = call.receive<RegisterRequest>()
            val user = UsersService.register(
                email = request.email,
                password = request.password,
                name = request.name
            )
            if (user == null) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse("Пользователь с таким email уже существует")
                )
                return@post
            }
            val token = generateToken(user.id, user.email)
            call.respond(HttpStatusCode.Created, AuthResponse(token, user))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val user = UsersService.login(
                email = request.email,
                password = request.password
            )
            if (user == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Неверный email или пароль")
                )
                return@post
            }
            val token = generateToken(user.id, user.email)
            call.respond(HttpStatusCode.OK, AuthResponse(token, user))
        }

        get("/hotels") {
            val city = call.request.queryParameters["city"]
            val maxPrice = call.request.queryParameters["maxPrice"]?.toDoubleOrNull()
            val hotels = HotelsService.getAll(city, maxPrice)
            call.respond(HttpStatusCode.OK, hotels)
        }

        get("/hotels/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Неверный id")
                )
            val hotel = HotelsService.getById(id)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Отель не найден")
                )
            call.respond(HttpStatusCode.OK, hotel)
        }

        authenticate("auth-jwt") {

            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                val user = UsersService.findById(userId)
                    ?: return@get call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("Пользователь не найден")
                    )
                call.respond(HttpStatusCode.OK, user)
            }

            post("/bookings") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                val request = call.receive<CreateBookingRequest>()
                val booking = BookingsService.create(
                    userId = userId,
                    hotelId = request.hotelId,
                    checkIn = request.checkIn,
                    checkOut = request.checkOut
                )
                if (booking == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Не удалось создать бронирование")
                    )
                    return@post
                }
                call.respond(HttpStatusCode.Created, booking)
            }

            get("/bookings/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                val bookings = BookingsService.getByUser(userId)
                call.respond(HttpStatusCode.OK, bookings)
            }
        }
    }
}