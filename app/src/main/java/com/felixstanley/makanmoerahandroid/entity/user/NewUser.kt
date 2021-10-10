package com.felixstanley.makanmoerahandroid.entity.user

data class NewUser(
    val email: String,
    val name: String,
    val password: String,
    val confirmPassword: String,
    val reCaptchaToken: String
)
