package com.project.balpyo.api.response

data class StorageListResponse(
    val code: String,
    val message: String,
    val result: List<StorageListResult>
)

data class StorageListResult(
    val id: Long,
    val content : String,
    val title: String,
    val secTime: Long,
    val voiceFilePath: String?,
    val isGenerating :Boolean,
    val playTime : Long,
    val originalScript : String,
    val speed : Long,
    val useAi: Boolean,
    val tags : List<String>,
    val topic : String,
    val keywords : String,
    val fcmToken : String,
    val speechMark : List<SpeechMark>
)
