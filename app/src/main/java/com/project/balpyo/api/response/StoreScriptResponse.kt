package com.project.balpyo.api.response

data class StoreScriptResponse(
    val code: String,
    val message: String,
    val result: StoreScriptResult,
)

data class StoreScriptResult(
    val script: String,
    val gptId: String,
    val uid: String,
    val title: String,
    val secTime: Long,
)
