package com.project.balpyo.Sign.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Sign.SignUpCompleteFragment
import com.project.balpyo.Sign.SignUpTermsFragment
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.SignInRequest
import com.project.balpyo.api.request.SignUpRequest
import com.project.balpyo.api.response.BaseResponse
import com.project.balpyo.api.response.SignInResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignViewModel: ViewModel() {
    fun signUp(fragment: SignUpTermsFragment, mainActivity: MainActivity) {
        var apiClient = ApiClient(mainActivity)

        apiClient.apiService.signUp(SignUpRequest(MyApplication.email, MyApplication.email, MyApplication.password))?.enqueue(object :
            Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: BaseResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())
                    fragment.findNavController().navigate(R.id.signUpCompleteFragment)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: BaseResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    fun signIn(fragment: SignUpCompleteFragment, mainActivity: MainActivity) {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.signIn(SignInRequest(MyApplication.email,MyApplication.password))?.enqueue(object :
            Callback<SignInResponse> {
            override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: SignInResponse? = response.body()
                    tokenManager.saveToken("${result?.token}")
                    Log.d("##", "onResponse 성공: " + result?.toString())
                    fragment.findNavController().navigate(R.id.homeFragment)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: SignInResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}
