package com.project.balpyo.Script.ViewModel

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.NotificationActivity
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.response.StorageDetailResponse
import com.project.balpyo.api.response.StorageDetailResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScriptDetailViewModel : ViewModel() {

    var scriptResult = MutableLiveData<StorageDetailResult>()

    fun getScriptDetail(mainActivity: NotificationActivity, scriptId: Int) {

        var tempList = mutableListOf<StorageDetailResult>()

        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.getStorageDetail("${tokenManager.getUid()}", scriptId)?.enqueue(object :
            Callback<StorageDetailResult> {
            override fun onResponse(call: Call<StorageDetailResult>, response: Response<StorageDetailResult>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: StorageDetailResult? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    scriptResult.value = result!!

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: StorageDetailResult? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<StorageDetailResult>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}