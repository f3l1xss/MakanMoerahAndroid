package com.felixstanley.makanmoerahandroid.entity.user

import com.felixstanley.makanmoerahandroid.entity.enums.Status

data class LoggedInUser(
    val email: String,
    val name: String,
    val avatarDirectory: String,
    val status: Status,
    val rejectReason: String,
    val authorities: List<GrantedAuthority>,
    val idNum: String?
)
