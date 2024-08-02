package com.project.balpyo.api

import com.project.balpyo.api.request.EditScriptRequest
import com.project.balpyo.api.request.GenerateAudioRequest
import retrofit2.Call
import com.project.balpyo.api.request.GenerateScriptRequest
import com.project.balpyo.api.request.ManageScriptRequest
import com.project.balpyo.api.request.SignInRequest
import com.project.balpyo.api.request.SignUpRequest
import com.project.balpyo.api.request.StoreScriptRequest
import com.project.balpyo.api.response.BaseResponse
import com.project.balpyo.api.response.GenerateAudioResponse
import com.project.balpyo.api.response.EditScriptResponse
import com.project.balpyo.api.response.GenerateScriptResponse
import com.project.balpyo.api.response.GenerateUidResponse
import com.project.balpyo.api.response.ManageScriptResponse
import com.project.balpyo.api.response.SignInResponse
import com.project.balpyo.api.response.StorageDetailResponse
import com.project.balpyo.api.response.StorageListResponse
import com.project.balpyo.api.response.StoreScriptResponse
import com.project.balpyo.api.response.VerifyUidResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
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
        @Header("UID") uid: String,
        @Body parameters: GenerateScriptRequest
    ): Call<GenerateScriptResponse>

    // 스크립트 저장
    @POST("every/manage/script")
    fun storeScript(
        @Header("UID") uid: String,
        @Body parameters: StoreScriptRequest
    ): Call<StoreScriptResponse>

    @POST("polly/uploadSpeech")
    fun generateAudio(
        @Header("Accept") accept: String,
        @Body parameters: GenerateAudioRequest
    ): Call<GenerateAudioResponse>

    // 보관함 리스트 조회
    @GET("every/manage/script/all")
    fun getStorageList(
        @Header("UID") uid: String
    ): Call<StorageListResponse>

    // 보관함 상세 조회
    @GET("every/manage/script/detail/{scriptId}")
    fun getStorageDetail(
        @Header("UID") uid: String,
        @Path("scriptId") scriptId:Int
    ): Call<StorageDetailResponse>

    // 스크립트 수정
    @PATCH("every/manage/script/detail/{scriptId}")
    fun editScript(
        @Header("UID") uid: String,
        @Path("scriptId") scriptId:Int,
        @Body parameters: EditScriptRequest
    ): Call<EditScriptResponse>

    // 스크립트 삭제
    @DELETE("every/manage/script/detail/{scriptId}")
    fun deleteScript(@Header("UID") uid: String, @Path("scriptId") scriptId: Int): Call<Void>

    @POST("api/auth/signup")
    fun signUp(@Body parameters: SignUpRequest): Call<BaseResponse>

    @POST("api/auth/signin")
    fun signIn(@Body parameters: SignInRequest): Call<SignInResponse>

    @POST("every/manage/script")
    fun manageScript(@Header("UID") uid: String, @Body parameters: ManageScriptRequest): Call<ManageScriptResponse>
}