package com.example

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object HotelsTable : Table("hotels") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val city = varchar("city", 255)
    val country = varchar("country", 255)
    val description = text("description")
    val pricePerNight = double("price_per_night")
    val rating = double("rating")
    val imageUrl = varchar("image_url", 500).nullable()
    override val primaryKey = PrimaryKey(id)
}

object BookingsTable : Table("bookings") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UsersTable.id)
    val hotelId = integer("hotel_id").references(HotelsTable.id)
    val checkIn = datetime("check_in")
    val checkOut = datetime("check_out")
    val totalPrice = double("total_price")
    override val primaryKey = PrimaryKey(id)
}