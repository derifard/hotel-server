package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Внутренняя ошибка сервера: ${cause.message}")
            )
        }
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Неверные данные: ${cause.message}")
            )
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(status, ErrorResponse("Ресурс не найден"))
        }
        status(HttpStatusCode.Unauthorized) { call, status ->
            call.respond(status, ErrorResponse("Необходима авторизация"))
        }
        status(HttpStatusCode.BadRequest) { call, status ->
            call.respond(status, ErrorResponse("Неверный запрос"))
        }
        status(HttpStatusCode.Forbidden) { call, status ->
            call.respond(status, ErrorResponse("Доступ запрещён"))
        }
    }
}