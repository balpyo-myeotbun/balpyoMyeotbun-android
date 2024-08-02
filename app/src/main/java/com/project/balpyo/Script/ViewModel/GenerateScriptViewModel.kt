package com.project.balpyo.Script.ViewModel

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.Utils.PreferenceUtil
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.GenerateScriptRequest
import com.project.balpyo.api.response.GenerateScriptResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerateScriptViewModel : ViewModel() {
    var script = MutableLiveData<String>()
    var gptId = MutableLiveData<String>()

    //대본 생성이 완료되면 결과 프래그먼트로 이동하도록 설정, 코루틴
    fun generateScript(fragment: Fragment, mainActivity: MainActivity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var apiClient = ApiClient(mainActivity)
                var tokenManager = TokenManager(mainActivity)

                MyApplication.preferences = PreferenceUtil(mainActivity)

                var inputScriptInfo = GenerateScriptRequest(
                    MyApplication.scriptId,
                    MyApplication.scriptTopic,
                    MyApplication.scriptSubtopic,
                    MyApplication.scriptTime,
                    "1234",
                    mutableListOf("SCRIPT"),
                    "false",
                    MyApplication.preferences.getFCMToken().toString()
                )

                Log.d("##", "script info : ${inputScriptInfo}")
                Log.d("발표몇분", "대본 생성 요청")

                apiClient.apiService.generateScript("Bearer ${PreferenceHelper.getUserToken(mainActivity)}", inputScriptInfo)?.enqueue(object : Callback<GenerateScriptResponse> {
                    override fun onResponse(
                        call: Call<GenerateScriptResponse>,
                        response: Response<GenerateScriptResponse>
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            var result: GenerateScriptResponse? = response.body()
                            Log.d("##", "onResponse 성공: " + result?.toString())

//                            MyApplication.preferences.setScriptResult(result?.result?.resultScript.toString())
//                            Log.d("발표몇분", "스크립트 저장 : ${MyApplication.preferences.getScriptResult()}")

                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            var result: GenerateScriptResponse? = response.body()
                            Log.d("##", "onResponse 실패")
                            Log.d("##", "onResponse 실패: " + response.code())
                            Log.d("##", "onResponse 실패: " + response.body())
                            val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                            Log.d("##", "Error Response: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<GenerateScriptResponse>, t: Throwable) {
                        // 통신 실패
                        Log.d("##", "onFailure 에러: " + t.message.toString());
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}