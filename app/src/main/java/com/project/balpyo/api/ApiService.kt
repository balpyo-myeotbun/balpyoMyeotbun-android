package com.project.balpyo.api

import com.project.balpyo.api.request.EditScriptRequestWithCalc
import com.project.balpyo.api.request.EditScriptRequestWithUnCalc
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.request.GenerateTimeAndFlowRequest
import com.project.balpyo.api.request.GenerateNoteRequest
import retrofit2.Call
import com.project.balpyo.api.request.GenerateScriptRequest
import com.project.balpyo.api.request.SignInRequest
import com.project.balpyo.api.request.SignUpRequest
import com.project.balpyo.api.response.BaseResponse
import com.project.balpyo.api.response.GenerateScriptResponse
import com.project.balpyo.api.response.GenerateUidResponse
import com.project.balpyo.api.response.SignInResponse
import com.project.balpyo.api.response.VerifyUidResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // UID 발급
    @POST("guest/manage/uid")
    fun generateUid(): Call<GenerateUidResponse>

    // UID 인증
    @GET("guest/manage/uid")
    fun verifyUid(
        @Header("UID") uid: String?
    ): Call<VerifyUidResponse>

    // 스크립트 생성
    @POST("user/ai/script")
    fun generateScript(
        @Header("Authorization") token: String,
        @Body parameters: GenerateScriptRequest
    ): Call<GenerateScriptResponse>

    @POST("polly/uploadSpeech")
    fun generateAudio(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String,
        @Body parameters: GenerateAudioRequest
    ): Call<BaseDto>

    // 보관함 리스트 조회
    @GET("scripts")
    fun getStorageList(
        @Header("Authorization") token: String,
    ): Call<List<BaseDto>>

    // 보관함 상세 조회
    @GET("scripts/{id}")
    fun getStorageDetail(
        @Header("Authorization") token: String,
        @Path("id") id :Long
    ): Call<BaseDto>

    // 스크립트 삭제
    @DELETE("scripts/{id}")
    fun deleteScript(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>

    //회원가입
    @POST("auth/signup")
    fun signUp(@Body parameters: SignUpRequest): Call<BaseResponse>

    //로그인
    @POST("auth/signin")
    fun signIn(@Body parameters: SignInRequest): Call<SignInResponse>

    //노트 생성
    @POST("scripts/note")
    fun generateNote(
        @Header("Authorization") token: String,
        @Body parameters: GenerateNoteRequest
    ): Call<BaseDto>

    //검색
    @GET("scripts/search")
    fun search(
        @Header("Authorization") token: String,
        @Query("tag") tag: String?,
        @Query("isGenerating") isGenerating: Boolean?,
        @Query("searchValue") searchValue: String?): Call<List<BaseDto>>

    //수정 (계산함)
    @PUT("scripts/{id}/cal")
    fun editAndCalc(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body parameters: EditScriptRequestWithCalc
    ): Call<BaseDto>

    //수정 (계산안함)
    @PUT("scripts/{id}/uncal")
    fun editWithUnCalc(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body parameters: EditScriptRequestWithUnCalc
    ): Call<BaseDto>

    @POST("scripts/time")
    fun postTimeAndFlow(
        @Header("Authorization") token: String,
        @Body parameters: GenerateTimeAndFlowRequest
    ): Call<BaseDto>
}