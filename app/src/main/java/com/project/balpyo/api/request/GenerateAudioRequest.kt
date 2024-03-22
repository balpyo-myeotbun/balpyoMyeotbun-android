package com.project.balpyo.api.request

import com.google.gson.annotations.SerializedName

data class GenerateAudioRequest (
    val text: String,
    val speed: Int,
    @SerializedName("balpyoAPIKey") val balpyoApikey: String
)
