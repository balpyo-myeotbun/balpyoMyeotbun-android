package com.project.balpyo.api.response

import com.google.gson.annotations.SerializedName

data class VerifyUidResponse(
    val code: String,
    val message: String,
    val result: VerifyUidResult,
)

data class VerifyUidResult(
    @SerializedName("yourUID") val yourUid: String,
    val verified: Boolean
)
