package com.felixstanley.makanmoerahandroid.fragment.account

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.felixstanley.makanmoerahandroid.MainApplication
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.network.service.UserService
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.runBlocking

class LoginViewModel(
    private val userService: UserService,
    private val sharedPreferences: SharedPreferences,
    private val loginEmailSharedPreferencesKey: String,
    private val loginPasswordSharedPreferencesKey: String
) : ViewModel() {

    // Boolean Flag to Inform Fragment that Login Button Has Been Clicked
    private val _loginButtonClicked = MutableLiveData<Boolean>()
    val loginButtonClicked: LiveData<Boolean>
        get() = _loginButtonClicked

    // Boolean Flag to Inform Fragment that Login is Complete and it needs to reload
    private val _loginCompleted = MutableLiveData<Boolean>()
    val loginCompleted: LiveData<Boolean>
        get() = _loginCompleted

    // Login Failure Flag
    private val _loginFailure = MutableLiveData<LoginFailure>()
    val loginFailure: LiveData<LoginFailure>
        get() = _loginFailure

    fun onLoginButtonClicked() {
        _loginButtonClicked.value = true
    }

    fun resetLoginButtonClickedFlag() {
        _loginButtonClicked.value = false
    }

    fun resetLoginCompletedFlag() {
        _loginCompleted.value = false
    }

    fun resetLoginFailureFlag() {
        _loginFailure.value = null
    }

    fun login(email: String, password: String) {
        // TODO: Start Loading here
        SafetyNet.getClient(MainApplication.getContext())
            .verifyWithRecaptcha(Constants.GOOGLE_RECAPTCHA_SITE_KEY)
            .addOnSuccessListener(OnSuccessListener { response ->
                // TODO: Stop Loading
                if (!response.tokenResult.isNullOrBlank()) {
                    runBlocking {
                        val loginResponse = userService.login(
                            email, password, response.tokenResult
                        )
                        if (loginResponse) {
                            // Successful Login
                            // Save Credentials to Shared Preferences for future relogin when session expires
                            saveLoginCredentialsToSharedPreferences(email, password)
                            // Trigger Login Completed Flag
                            _loginCompleted.value = true
                        } else {
                            // Failed Login Response (Invalid Credentials)
                            _loginFailure.value = LoginFailure.INVALID_CREDENTIALS
                        }
                    }
                } else {
                    // Null or Blank Token Result
                    _loginFailure.value = LoginFailure.EMPTY_RECAPTCHA_RESPONSE
                }
            })
            .addOnFailureListener(OnFailureListener { e ->
                // TODO: Stop Loading
                // Failed Recaptcha Response
                _loginFailure.value = LoginFailure.FAILED_RECAPTCHA
            })
    }

    private fun saveLoginCredentialsToSharedPreferences(email: String, password: String) {
        sharedPreferences.edit {
            putString(loginEmailSharedPreferencesKey, email)
            putString(loginPasswordSharedPreferencesKey, password)
        }
    }

}