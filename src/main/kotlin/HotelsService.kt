package com.example

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object HotelsService {

    fun getAll(city: String? = null, maxPrice: Double? = null): List<Hotel> {
        return transaction {
            var query = HotelsTable.selectAll()

            if (city != null) {
                query = query.andWhere { HotelsTable.city like "%$city%" }
            }
            if (maxPrice != null) {
                query = query.andWhere { HotelsTable.pricePerNight lessEq maxPrice }
            }

            query.map { row ->
                Hotel(
                    id = row[HotelsTable.id],
                    name = row[HotelsTable.name],
                    city = row[HotelsTable.city],
                    country = row[HotelsTable.country],
                    description = row[HotelsTable.description],
                    pricePerNight = row[HotelsTable.pricePerNight],
                    rating = row[HotelsTable.rating],
                    imageUrl = row[HotelsTable.imageUrl]
                )
            }
        }
    }

    fun getById(id: Int): Hotel? {
        return transaction {
            HotelsTable.select { HotelsTable.id eq id }
                .singleOrNull()
                ?.let { row ->
                    Hotel(
                        id = row[HotelsTable.id],
                        name = row[HotelsTable.name],
                        city = row[HotelsTable.city],
                        country = row[HotelsTable.country],
                        description = row[HotelsTable.description],
                        pricePerNight = row[HotelsTable.pricePerNight],
                        rating = row[HotelsTable.rating],
                        imageUrl = row[HotelsTable.imageUrl]
                    )
                }
        }
    }

    fun seedHotels() {
        transaction {
            if (HotelsTable.selectAll().empty()) {
                val hotels = listOf(
                    Triple("Grand Hotel Paris", "Paris", "France"),
                    Triple("Berlin Plaza", "Berlin", "Germany"),
                    Triple("Roma Luxury", "Rome", "Italy"),
                    Triple("Barcelona Suites", "Barcelona", "Spain"),
                    Triple("Amsterdam Inn", "Amsterdam", "Netherlands"),
                    Triple("Vienna Palace", "Vienna", "Austria"),
                    Triple("Prague Central", "Prague", "Czech Republic"),
                    Triple("Budapest Royal", "Budapest", "Hungary")
                )
                val descriptions = listOf(
                    "Роскошный отель в самом сердце города с видом на исторические достопримечательности.",
                    "Современный отель с превосходным сервисом и удобным расположением.",
                    "Уютный бутик-отель с авторским дизайном и отличной кухней.",
                    "Элегантный отель с бассейном, спа и панорамным рестораном."
                )
                hotels.forEachIndexed { index, (name, city, country) ->
                    HotelsTable.insert {
                        it[HotelsTable.name] = name
                        it[HotelsTable.city] = city
                        it[HotelsTable.country] = country
                        it[description] = descriptions[index % descriptions.size]
                        it[pricePerNight] = (80 + index * 25).toDouble()
                        it[rating] = 3.5 + (index % 5) * 0.3
                        it[imageUrl] = null
                    }
                }
            }
        }
    }
}