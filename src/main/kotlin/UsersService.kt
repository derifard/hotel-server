package com.example

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt


//Сервис пользователя
object UsersService {

    fun register(email: String, password: String, name: String): User? {
        val exists = transaction {
            !UsersTable.select { UsersTable.email eq email }.empty()
        }
        if (exists) return null

        val hash = BCrypt.hashpw(password, BCrypt.gensalt())

        return transaction {
            val id = UsersTable.insert {
                it[UsersTable.email] = email
                it[passwordHash] = hash
                it[UsersTable.name] = name
            } get UsersTable.id

            User(id = id, email = email, name = name)
        }
    }

    fun login(email: String, password: String): User? {
        return transaction {
            val row = UsersTable.select { UsersTable.email eq email }
                .singleOrNull() ?: return@transaction null

            val hash = row[UsersTable.passwordHash]
            if (!BCrypt.checkpw(password, hash)) return@transaction null

            User(
                id = row[UsersTable.id],
                email = row[UsersTable.email],
                name = row[UsersTable.name]
            )
        }
    }

    fun findById(id: Int): User? {
        return transaction {
            UsersTable.select { UsersTable.id eq id }
                .singleOrNull()
                ?.let {
                    User(
                        id = it[UsersTable.id],
                        email = it[UsersTable.email],
                        name = it[UsersTable.name]
                    )
                }
        }
    }
}