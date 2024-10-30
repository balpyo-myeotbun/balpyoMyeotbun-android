package com.project.balpyo.api

import com.project.balpyo.api.response.SpeechMark
import com.project.balpyo.api.response.StorageListResult

data class BaseDto (
    val id: Long?,
    val content: String?,
    val title: String?,
    val secTime: Long?,
    val voiceFilePath: String?,
    val isGenerating :Boolean?,
    val playTime : Long?,
    val originalScript : String?,
    val speed : Long?,
    val useAi: Boolean?,
    val tags : List<String>?,
    val topic : String?,
    val keywords : String?,
    val fcmToken : String?,
    val speechMark : List<SpeechMark>?
)
fun BaseDto.toStorageListResult(): StorageListResult {
    return StorageListResult(
        id = this.id ?: -1,
        content = this.content ?: "",
        title = this.title.orEmpty(),
        secTime = this.secTime ?: 0,
        voiceFilePath = this.voiceFilePath,
        isGenerating = this.isGenerating ?: false,
        playTime = this.playTime ?: 0,
        originalScript = this.originalScript.orEmpty(),
        speed = this.speed?: 0,
        useAi = this.useAi?: false,
        tags = this.tags.orEmpty(),
        topic = this.topic.orEmpty(),
        keywords = this.keywords.orEmpty(),
        fcmToken = this.fcmToken.orEmpty(),
        speechMark = this.speechMark.orEmpty()
    )
}