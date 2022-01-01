package com.felixstanley.makanmoerahandroid.network.interceptor

import android.content.Context
import com.felixstanley.makanmoerahandroid.MainActivity
import com.felixstanley.makanmoerahandroid.MainApplication
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import com.felixstanley.makanmoerahandroid.utility.Utility
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/*

Interceptor which will attempt to perform relogin when response
from backend is successful and contains Location Header
of Session Expired URL

 */

private const val LOCATION_HEADER_NAME = "Location"
private const val SESSION_EXPIRED_URL = "/login?sessionExpired=true"

class ReloginInterceptor : Interceptor {

    private val sharedPreferences = MainActivity.getInstance().getPreferences(Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (Utility.credentialsExistAtSharedPreferences(sharedPreferences) && response.isSuccessful
            && response.header(LOCATION_HEADER_NAME).equals(SESSION_EXPIRED_URL)
        ) {
            // Expired Session & Credentials Exist at Shared Preferences, User has to relogin
            try {
                relogin()
            } catch (e: Exception) {
                // TODO: Find a way to warn user that relogin is unsuccessful
                Timber.e(e, "exception occured" + e)
                return response
            }
            // Repeat Chain Processing as now User has relogin
            response.close()
            return chain.proceed(chain.request())
        }
        return response
    }

    private fun relogin() {
        val response = SafetyNet.getClient(MainApplication.getContext())
            .verifyWithRecaptcha(Constants.GOOGLE_RECAPTCHA_SITE_KEY)
        Tasks.await(response)

        if (response.isSuccessful && !response.result.tokenResult.isNullOrBlank()) {
            runBlocking {
                val email = sharedPreferences.getString(
                    Constants.SHARED_PREFERENCES_LOGIN_EMAIL_KEY,
                    null
                )!!
                val password = sharedPreferences.getString(
                    Constants.SHARED_PREFERENCES_LOGIN_PASSWORD_KEY,
                    null
                )!!
                NetworkApi.userService.login(
                    email, password, response.result.tokenResult
                )
            }
        }
    }

}