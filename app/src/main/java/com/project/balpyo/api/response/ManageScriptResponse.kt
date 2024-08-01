package com.project.balpyo.api.response

data class ManageScriptResponse(
    val code: String,
    val message: String,
    val result: ManageScrpitResult
)

data class ManageScrpitResult(
    var scriptId: Long,
    var script: String?,
    var gptId: Long?,
    var uid: Long?,
    var title: String?,
    var secTime: Long?,
    var voiceFilePath: String?,
    var useAi: Boolean,
    var generating: Boolean
)
