package com.project.balpyo.api

import retrofit2.Call
import com.project.balpyo.api.request.GenerateScriptRequest
import com.project.balpyo.api.response.GenerateScriptResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // 스크립트 생성
    @POST("user/ai/script")
    fun generateScript(
        @Body parameters: GenerateScriptRequest
    ): Call<GenerateScriptResponse>
}