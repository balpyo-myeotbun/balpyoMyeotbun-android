package com.project.balpyo.api.request

data class ManageScriptRequest(
    var script: String,
    var title: String,
    var secTime: Long,
    var tag: List<String>,
    var useAi: Boolean
)
