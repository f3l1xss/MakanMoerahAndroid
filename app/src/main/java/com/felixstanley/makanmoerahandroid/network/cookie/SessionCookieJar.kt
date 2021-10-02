package com.felixstanley.makanmoerahandroid.network.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl


object SessionCookieJar : CookieJar {

    var cookies: MutableList<Cookie> = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        // Always save All Cookies
        this.cookies = cookies
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        // Always forward 'saved' cookies
        return cookies
    }

}