package com.example

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureStatusPages()
    DatabaseFactory.init()
    configureRouting()
}