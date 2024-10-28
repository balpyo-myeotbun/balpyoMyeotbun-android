package com.project.balpyo.api.request

import com.project.balpyo.api.response.SpeechMark

data class GenerateNoteRequest(
    val content: String,
    val title: String,
    val voiceFilePath: String,
    val originalScript : String,
    val speed : Long,
    val useAi: Boolean,
    val tags : List<String>,
    val speechMark: List<SpeechMark>
)