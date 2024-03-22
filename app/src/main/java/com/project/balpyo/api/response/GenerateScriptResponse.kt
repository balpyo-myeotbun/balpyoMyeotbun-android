package com.project.balpyo.api.response

import com.google.gson.annotations.SerializedName

data class GenerateScriptResponse(
    val code: String,
    val message: String,
    val result: GenerateScrpitResult
)

data class GenerateScrpitResult(
    val resultScript: List<ResultScript>,
    val gptId: String
)

data class ResultScript(
    val index: Long,
    val message: Message,
    val logprobs: Any?,
    @SerializedName("finish_reason") val finishReason: String
)

data class Message(
    val role: String,
    val content: String
)