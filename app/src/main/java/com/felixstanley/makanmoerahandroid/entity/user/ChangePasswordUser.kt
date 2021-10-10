package com.felixstanley.makanmoerahandroid.entity.user

data class ChangePasswordUser(
    val newPassword: String,
    val oldPassword: String,
    val recaptchaToken: String
)
