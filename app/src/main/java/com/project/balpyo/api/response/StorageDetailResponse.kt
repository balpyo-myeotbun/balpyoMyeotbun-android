package com.project.balpyo.api.response

data class StorageDetailResponse(
    val code: String,
    val message: String,
    val result: StorageDetailResult,
)

data class StorageDetailResult(
    val scriptId: Long,
    val script: String,
    val gptId: String?,
    val uid: String?,
    val title: String,
    val secTime: Long,
    val voiceFilePath : String?,
    val useAi : Boolean,
    val tag : List<String>?,
    val generating : Boolean
)
