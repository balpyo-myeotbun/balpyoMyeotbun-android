package com.project.balpyo.api.response

data class StorageListResponse(
    val code: String,
    val message: String,
    val result: List<StorageListResult>
)

data class StorageListResult(
    val scriptId: Long,
    val script: String?,
    val gptId: String?,
    val uid: String,
    val title: String,
    val secTime: Long,
    val voiceFilePath: String?,
    val tag : List<String>? = listOf("note") //테스트 코드, 추후 api 명세나오면 수정
)
