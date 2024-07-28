package com.project.balpyo.api.response

data class SignInResponse(
    val token: String,
    val type: String,
    val id: Long,
    val username: String,
    val email : String,
    val roles : List<String>
)