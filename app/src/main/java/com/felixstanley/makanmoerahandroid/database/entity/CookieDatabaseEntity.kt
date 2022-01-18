package com.felixstanley.makanmoerahandroid.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import okhttp3.Cookie

@Entity(tableName = "cookie")
data class CookieDatabaseEntity(
    @PrimaryKey
    val name: String,
    val value: String,
    val expiresAt: Long,
    val domain: String,
    val path: String,
    val secure: Boolean,
    val httpOnly: Boolean,
    val hostOnly: Boolean,
    val persistent: Boolean
)

// Extension Function to map CookieDatabaseEntity to Cookie
fun List<CookieDatabaseEntity>.asDomainModel(): List<Cookie> {
    return map {
        val cookieBuilder = Cookie.Builder()
        cookieBuilder.name(it.name).value(it.value).expiresAt(it.expiresAt).path(it.path)

        if (it.hostOnly) {
            cookieBuilder.hostOnlyDomain(it.domain)
        } else {
            cookieBuilder.domain(it.domain)
        }

        if (it.secure) {
            cookieBuilder.secure()
        }

        if (it.httpOnly) {
            cookieBuilder.httpOnly()
        }
        cookieBuilder.build()
    }
}

// Extension Function to map Cookie to CookieDatabaseEntity
fun Cookie.asDatabaseModel(): CookieDatabaseEntity {
    return CookieDatabaseEntity(
        name(),
        value(),
        expiresAt(),
        domain(),
        path(),
        secure(),
        httpOnly(),
        hostOnly(),
        persistent()
    )
}
