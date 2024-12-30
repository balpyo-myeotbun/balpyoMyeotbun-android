package com.project.balpyo.Script.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScriptDetailViewModel : ViewModel() {

    var scriptResult = MutableLiveData<BaseDto>()

    fun getScriptDetail(mainActivity: MainActivity, scriptId: Long) {

        val apiClient = ApiClient(mainActivity)
        val tokenManager = TokenManager(mainActivity)

        apiClient.apiService.getStorageDetail("${tokenManager.getUid()}", scriptId).enqueue(
            object :
                Callback<BaseDto> {
                override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        var result: BaseDto? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())

                        scriptResult.value = result!!

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseDto? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseDto>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}