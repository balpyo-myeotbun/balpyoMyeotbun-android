package com.project.balpyo.api.response

data class GenerateScriptResponse(
    val code: String,
    val message: String,
    val result: GenerateScrpitResult
)

data class GenerateScrpitResult(
    val id: Int,
    val content: String,
    val title: String,
    val secTime: Int,
    val voiceFilePath: String,
    val isGenerating: Boolean,
    val playTime: Int,
    val originalScript: String,
    val speed: Int,
    val useAi: Boolean,
    val tags: List<String>,
    val topic: String,
    val keywords: String,
    val fcmToken: String
)