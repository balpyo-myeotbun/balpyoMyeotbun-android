package com.project.balpyo.api.request

data class StoreScriptRequest(
    val script: String,
    val gptId: String,
    val title: String,
    val secTime: Long
)
