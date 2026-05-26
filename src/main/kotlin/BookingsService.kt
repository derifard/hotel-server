package com.example

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object BookingsService {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun create(userId: Int, hotelId: Int, checkIn: String, checkOut: String): Booking? {
        if (checkIn.isBlank() || checkOut.isBlank()) return null

        val hotel = HotelsService.getById(hotelId) ?: return null

        return try {
            val checkInDate = LocalDateTime.parse(checkIn, formatter)
            val checkOutDate = LocalDateTime.parse(checkOut, formatter)

            if (checkOutDate <= checkInDate) return null

            val days = java.time.Duration.between(checkInDate, checkOutDate).toDays()
            val totalPrice = hotel.pricePerNight * days

            transaction {
                val id = BookingsTable.insert {
                    it[BookingsTable.userId] = userId
                    it[BookingsTable.hotelId] = hotelId
                    it[BookingsTable.checkIn] = checkInDate
                    it[BookingsTable.checkOut] = checkOutDate
                    it[BookingsTable.totalPrice] = totalPrice
                } get BookingsTable.id

                Booking(
                    id = id,
                    userId = userId,
                    hotelId = hotelId,
                    checkIn = checkIn,
                    checkOut = checkOut,
                    totalPrice = totalPrice
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getByUser(userId: Int): List<Booking> {
        return transaction {
            (BookingsTable innerJoin HotelsTable)
                .select { BookingsTable.userId eq userId }
                .map { row ->
                    Booking(
                        id = row[BookingsTable.id],
                        userId = row[BookingsTable.userId],
                        hotelId = row[BookingsTable.hotelId],
                        hotelName = row[HotelsTable.name],
                        hotelCity = row[HotelsTable.city],
                        checkIn = row[BookingsTable.checkIn].format(formatter),
                        checkOut = row[BookingsTable.checkOut].format(formatter),
                        totalPrice = row[BookingsTable.totalPrice]
                    )
                }
        }
    }
}