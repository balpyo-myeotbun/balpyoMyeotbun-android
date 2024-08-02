package com.project.balpyo.api.request

data class StoreScriptRequest(
    val script: String,
    val gptId: String,
    val title: String,
    val tag: List<String>,
    val secTime: Long
)
