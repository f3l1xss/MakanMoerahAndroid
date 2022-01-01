package com.felixstanley.makanmoerahandroid.fragment.account

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.felixstanley.makanmoerahandroid.network.service.UserService

class AccountViewModelFactory(
    private val userService: UserService,
    private val sharedPreferences: SharedPreferences,
    private val loginEmailSharedPreferencesKey: String,
    private val loginPasswordSharedPreferencesKey: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(
                userService,
                sharedPreferences,
                loginEmailSharedPreferencesKey,
                loginPasswordSharedPreferencesKey
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}