package com.project.balpyo.api.response

import com.google.gson.annotations.SerializedName

data class GenerateAudioResponse (
    val profileUrl : String,
    val playTime: Int,
    val speechMarks: List<SpeechMark>
)

data class SpeechMark(
    @SerializedName("start") var start: Int, // 초 단위
    @SerializedName("end") var end: Int, // 초 단위
    @SerializedName("time") val time: Int,
    @SerializedName("type") val type: String,
    @SerializedName("value") var value: String
)