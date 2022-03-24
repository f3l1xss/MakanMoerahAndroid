package com.felixstanley.makanmoerahandroid.network.cookie

import com.felixstanley.makanmoerahandroid.MainActivity
import com.felixstanley.makanmoerahandroid.database.CookieDatabase
import com.felixstanley.makanmoerahandroid.database.dao.CookieDatabaseDao
import com.felixstanley.makanmoerahandroid.database.entity.asDatabaseModel
import com.felixstanley.makanmoerahandroid.database.entity.asDomainModel
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl


object CookieJarImpl : CookieJar {

    private var cookieDatabaseDao: CookieDatabaseDao
    var cookies: List<Cookie> = emptyList()

    init {
        cookieDatabaseDao =
            CookieDatabase.getInstance(MainActivity.getInstance().application).cookieDatabaseDao
        retrieveLatestCookiesFromDb()
    }

    override fun saveFromResponse(url: HttpUrl, newCookies: MutableList<Cookie>) {
        // Update and Save All Cookies
        for (newCookie in newCookies.listIterator()) {
            insertNewCookie(newCookie)
        }
        retrieveLatestCookiesFromDb()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        // Always forward 'saved' cookies
        return cookies
    }

    fun insertNewCookie(cookie: Cookie) {
        runBlocking {
            if (cookies.any { cookie -> cookie.name().equals(cookie.name()) }) {
                cookieDatabaseDao.deleteByName(cookie.name())
            }
            cookieDatabaseDao.insert(cookie.asDatabaseModel())
        }
    }

    // Retrieve Latest Cookies From DB Synchronously
    fun retrieveLatestCookiesFromDb() {
        cookies = cookieDatabaseDao.getAllCookiesSynchronous().asDomainModel()
    }

    suspend fun clearCookies() {
        // Clear All Saved Cookies (Called upon logout so that subsequent requests are not treated as session expired request)
        // Need to perform this explicitly as we do not consider setting expired cookie as deleting that cookie
        // similar to browser behavior
        cookieDatabaseDao.clear()
        retrieveLatestCookiesFromDb()
    }

}