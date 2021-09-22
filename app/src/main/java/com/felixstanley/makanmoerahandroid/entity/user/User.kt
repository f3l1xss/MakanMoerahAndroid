package com.felixstanley.makanmoerahandroid.entity.user

data class User(
    val email: String,
    val password: String,
    val recaptchaToken: String
)