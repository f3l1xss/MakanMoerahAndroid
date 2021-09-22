package com.felixstanley.makanmoerahandroid.network.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

private const val LOGIN_API_URL = "/api/user/login"

class SessionCookieJar : CookieJar {

    private var cookies: MutableList<Cookie> = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        // Set All Cookies if we are getting response from Login API
        if (url.encodedPath().equals(LOGIN_API_URL)) {
            this.cookies = cookies
        }
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        // Always forward 'saved' cookies whenever we are calling non Login API
        if (!url.encodedPath().equals(LOGIN_API_URL) && !cookies.isNullOrEmpty()) {
            return cookies
        }
        return mutableListOf<Cookie>()
    }

}