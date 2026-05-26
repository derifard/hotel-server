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
                val images = listOf(
                    "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=400",
                    "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400",
                    "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=400",
                    "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=400",
                    "https://images.unsplash.com/photo-1551882547-ff40c4a49ce8?w=400",
                    "https://images.unsplash.com/photo-1455587734955-081b22074882?w=400",
                    "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=400",
                    "https://images.unsplash.com/photo-1549294413-26f195471c9b?w=400"
                )
                hotels.forEachIndexed { index, (name, city, country) ->
                    HotelsTable.insert {
                        it[HotelsTable.name] = name
                        it[HotelsTable.city] = city
                        it[HotelsTable.country] = country
                        it[description] = descriptions[index % descriptions.size]
                        it[pricePerNight] = (80 + index * 25).toDouble()
                        it[rating] = 3.5 + (index % 5) * 0.3
                        it[imageUrl] = images[index]
                    }
                }
            }
        }
    }

    fun updateHotelImages() {
        val images = listOf(
            "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=400",
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400",
            "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=400",
            "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=400",
            "https://images.unsplash.com/photo-1551882547-ff40c4a49ce8?w=400",
            "https://images.unsplash.com/photo-1455587734955-081b22074882?w=400",
            "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=400",
            "https://images.unsplash.com/photo-1549294413-26f195471c9b?w=400"
        )
        transaction {
            val hotels = HotelsTable.selectAll().toList()
            hotels.forEachIndexed { index, row ->
                HotelsTable.update({ HotelsTable.id eq row[HotelsTable.id] }) {
                    it[imageUrl] = images[index % images.size]
                }
            }
        }
    }
}