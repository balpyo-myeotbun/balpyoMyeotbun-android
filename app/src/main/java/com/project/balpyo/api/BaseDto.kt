package com.project.balpyo.api

import com.project.balpyo.api.response.SpeechMark

data class BaseDto (
    val id: Long?,
    val content: String?,
    val title: String?,
    val secTime: Int?,
    val voiceFilePath: String?,
    val isGenerating :Boolean?,
    val playTime : Int?,
    val originalScript : String?,
    val speed : Int?,
    val useAi: Boolean?,
    val tags : List<String>?,
    val topic : String?,
    val keywords : String?,
    val fcmToken : String?,
    val speechMark : List<SpeechMark>?
)