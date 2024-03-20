package com.project.balpyo.api.response

import com.google.gson.annotations.SerializedName

data class GenerateScriptResponse(
    val code: String,
    val message: String,
    val result: List<Result>
)

data class Result(
    val index: Long,
    val message: Message,
    val logprobs: Any?,
    @SerializedName("finish_reason") val finishReason: String
)

data class Message(
    val role: String,
    val content: String
)

