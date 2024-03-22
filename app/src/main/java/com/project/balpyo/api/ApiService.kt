package com.project.balpyo.api

import retrofit2.Call
import com.project.balpyo.api.request.GenerateScriptRequest
import com.project.balpyo.api.response.GenerateScriptResponse
import com.project.balpyo.api.response.GenerateUidResponse
import com.project.balpyo.api.response.VerifyUidResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    // UID 발급
    @POST("guest/manage/uid")
    fun generateUid(): Call<GenerateUidResponse>

    // UID 인증
    @GET("guest/manage/uid")
    fun verifyUid(
        @Header("UID") uid: String
    ): Call<VerifyUidResponse>

    // 스크립트 생성
    @POST("user/ai/script")
    fun generateScript(
        @Body parameters: GenerateScriptRequest
    ): Call<GenerateScriptResponse>
}