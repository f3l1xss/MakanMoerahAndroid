package com.felixstanley.makanmoerahandroid.fragment.account

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixstanley.makanmoerahandroid.MainApplication
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.entity.user.LoggedInUser
import com.felixstanley.makanmoerahandroid.network.Configuration.moshi
import com.felixstanley.makanmoerahandroid.network.cookie.CookieJarImpl
import com.felixstanley.makanmoerahandroid.network.service.UserService
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AccountViewModel(
    private val userService: UserService,
    private val sharedPreferences: SharedPreferences,
    private val loginEmailSharedPreferencesKey: String,
    private val loginPasswordSharedPreferencesKey: String
) : ViewModel() {

    private val _loggedInUser = MutableLiveData<LoggedInUser>()
    val loggedInUser: LiveData<LoggedInUser>
        get() = _loggedInUser

    // Boolean Flag to Inform Fragment that Logout is Complete
    private val _logoutCompleted = MutableLiveData<Boolean>()
    val logoutCompleted: LiveData<Boolean>
        get() = _logoutCompleted

    // Logout Failure Flag
    private val _logoutFailure = MutableLiveData<LogoutFailure>()
    val logoutFailure: LiveData<LogoutFailure>
        get() = _logoutFailure

    // Relogin Failure Flag
    private val _reloginFailure = MutableLiveData<LoginFailure>()
    val reloginFailure: LiveData<LoginFailure>
        get() = _reloginFailure

    init {
        getLoggedInUser()
    }

    private fun getLoggedInUser() {
        viewModelScope.launch {
            val responseBody = userService.getLoggedInUser()
            val responseContent = responseBody.string()
            if (responseContent.isNullOrBlank()) {
                // Indicates that Backend API return null (session is gone for some reason
                // (should not happen as User with Expired Session will be relogged in with reloginInterceptor))
                // and that User needs to be relogged In using credentials at Shared Preferences)
                relogin()
            } else {
                // Indicates that valid LoggedInUser Instance is returned, parse the responseBody to LoggedInUser POJO
                val loggedInUser =
                    moshi.adapter(LoggedInUser::class.java).fromJson(responseContent)!!
                // Update our LiveData with fetched loggedInUser
                _loggedInUser.value = loggedInUser
            }
        }
    }

    private fun relogin() {
        SafetyNet.getClient(MainApplication.getContext())
            .verifyWithRecaptcha(Constants.GOOGLE_RECAPTCHA_SITE_KEY)
            .addOnSuccessListener(OnSuccessListener { response ->
                if (!response.tokenResult.isNullOrBlank()) {
                    runBlocking {
                        val email = sharedPreferences.getString(
                            Constants.SHARED_PREFERENCES_LOGIN_EMAIL_KEY,
                            null
                        )!!
                        val password = sharedPreferences.getString(
                            Constants.SHARED_PREFERENCES_LOGIN_PASSWORD_KEY,
                            null
                        )!!
                        val loginResponse = userService.login(
                            email, password, response.tokenResult
                        )
                        if (loginResponse) {
                            // Successful ReLogin
                            // Try to getLoggedInUser again
                            getLoggedInUser()
                        } else {
                            // Failed Login Response (Invalid Credentials)
                            _reloginFailure.value = LoginFailure.INVALID_CREDENTIALS
                        }
                    }
                } else {
                    // Null or Blank Token Result
                    _reloginFailure.value = LoginFailure.EMPTY_RECAPTCHA_RESPONSE
                }
            })
            .addOnFailureListener(OnFailureListener { e ->
                // Failed Recaptcha Response
                _reloginFailure.value = LoginFailure.FAILED_RECAPTCHA
            })
    }

    fun onLogoutButtonClicked() {
        logout()
    }

    private fun logout() {
        // TODO: Start Loading
        viewModelScope.launch {
            try {
                userService.logout()
                CookieJarImpl.clearCookies()
                deleteLoginCredentialsFromSharedPreferences()
                // Trigger Logout Completed Flag
                _logoutCompleted.value = true
                // TODO: Stop Loading
                // TODO: Inform User that logout is successful
            } catch (e: Exception) {
                // Logout fails
                _logoutFailure.value = LogoutFailure.LOGOUT_GENERAL_EXCEPTION
            }
        }
    }

    fun resetLogoutCompletedFlag() {
        _logoutCompleted.value = false
    }

    fun resetLogoutFailureFlag() {
        _logoutFailure.value = null
    }

    fun resetReloginFailureFlag() {
        _reloginFailure.value = null
    }

    private fun deleteLoginCredentialsFromSharedPreferences() {
        sharedPreferences.edit {
            remove(loginEmailSharedPreferencesKey)
            remove(loginPasswordSharedPreferencesKey)
        }
    }

}