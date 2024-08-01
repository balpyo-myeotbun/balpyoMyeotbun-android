package com.project.balpyo.api.request

import com.google.gson.annotations.SerializedName

data class GenerateScriptRequest(
    val scriptId: Long,
    val topic: String,
    val keywords: String,
    val secTime: Long,
    @SerializedName("balpyoAPIKey") val balpyoApikey: String,
    val test: String,
    val fcmtoken: String
)
