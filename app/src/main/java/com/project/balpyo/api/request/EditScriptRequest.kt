package com.project.balpyo.api.request

data class EditScriptRequest(
    val scriptId: Long,
    val script: String,
    val title: String,
    val secTime: Long
)
