package com.felixstanley.makanmoerahandroid.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/*

 Interceptor which will add 'X-Requested-With' = XMLHttpRequest Request Header to indicate
  that request should be treated as Ajax request

 */


private const val AJAX_HEADER_NAME = "X-Requested-With"
private const val AJAX_HEADER_VALUE = "XMLHttpRequest"

class AjaxHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().addHeader(AJAX_HEADER_NAME, AJAX_HEADER_VALUE).build()
        return chain.proceed(request)
    }

}