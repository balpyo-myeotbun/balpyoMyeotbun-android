package com.project.balpyo.api.request

import com.google.gson.annotations.SerializedName

data class GenerateScriptRequest(
    val title: String,
    val topic: String,
    val keywords: String,
    val secTime: Long,
    val fcmToken: String
)
