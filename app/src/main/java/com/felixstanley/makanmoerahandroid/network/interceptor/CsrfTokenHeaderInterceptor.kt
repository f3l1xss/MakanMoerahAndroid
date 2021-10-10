package com.felixstanley.makanmoerahandroid.network.interceptor

import com.felixstanley.makanmoerahandroid.network.cookie.CookieJarImpl
import okhttp3.Cookie
import okhttp3.Interceptor
import okhttp3.Response


/*

Interceptor which will add 'X-XSRF-TOKEN' Request Header taken from 'XSRF-TOKEN' Cookie
to HTTP Method other than "GET", "HEAD", "TRACE", "OPTIONS"

 */

private val allowedHttpMethods = listOf("GET", "HEAD", "TRACE", "OPTIONS")
private const val CSRF_COOKIE_NAME = "XSRF-TOKEN"
private const val CSRF_HEADER_NAME = "X-XSRF-TOKEN"

class CsrfTokenHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!allowedHttpMethods.contains(request.method())) {
            val csrfCookieValue = getCsrfCookieValue(CookieJarImpl.cookies)
            if (csrfCookieValue != null) {
                request = request.newBuilder().addHeader(CSRF_HEADER_NAME, csrfCookieValue).build()
            }
        }
        return chain.proceed(request)
    }

    // Lookup for Cookie with 'CSRF_COOKIE_NAME' if any and return its value
    // Otherwise, return null
    private fun getCsrfCookieValue(cookies: List<Cookie>): String? {
        for (cookie in cookies.listIterator()) {
            if (cookie.name().equals(CSRF_COOKIE_NAME)) {
                return cookie.value()
            }
        }
        return null
    }

}