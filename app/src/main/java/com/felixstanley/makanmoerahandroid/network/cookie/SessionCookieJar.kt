package com.felixstanley.makanmoerahandroid.network.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl


object SessionCookieJar : CookieJar {

    var cookies: MutableList<Cookie> = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        // Update and Save All Cookies
        for (newCookie in cookies.listIterator()) {
            this.cookies.removeIf({ cookie -> cookie.name().equals(newCookie.name()) })
            this.cookies.add(newCookie)
        }
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        // Always forward 'saved' cookies
        return cookies
    }

}