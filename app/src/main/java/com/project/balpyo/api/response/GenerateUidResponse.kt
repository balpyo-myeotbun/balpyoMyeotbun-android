package com.project.balpyo.api.response

data class GenerateUidResponse(
    val code: String,
    val message: String,
    val result: UidResult
)

data class UidResult(
    val uid: String
)
