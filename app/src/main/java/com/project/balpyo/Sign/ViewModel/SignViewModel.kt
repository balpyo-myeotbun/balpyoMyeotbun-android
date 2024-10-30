package com.project.balpyo.Sign.ViewModel

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceHelper
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
    var signInResponse = MutableLiveData<SignInResponse>()
    fun signUp(fragment: Fragment, mainActivity: MainActivity) {
        val apiClient = ApiClient(mainActivity)

        apiClient.apiService.signUp(SignUpRequest(MyApplication.email, MyApplication.password))
            .enqueue(object :
                Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())
                        fragment.findNavController().navigate(R.id.signUpCertificationFragment)
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

    fun signIn(fragment: Fragment, mainActivity: MainActivity) {
        val apiClient = ApiClient(mainActivity)
        val tokenManager = TokenManager(mainActivity)

        apiClient.apiService.signIn(SignInRequest(MyApplication.email,MyApplication.password))
            .enqueue(object :
                Callback<SignInResponse> {
                override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: SignInResponse? = response.body()
                        tokenManager.saveToken("${result?.token}")
                        Log.d("##", "onResponse 성공: " + result?.toString())
                        signInResponse.value = result!!

                        PreferenceHelper.saveUserToken(mainActivity, result.token)
                        PreferenceHelper.saveUserId(mainActivity, result.email)
                        PreferenceHelper.saveUserNickname(mainActivity, result.username)
                        PreferenceHelper.saveUserType(mainActivity, "email")

                        /* TODO: 추후 인증 등급 확인 시 다시 추가할 예정
                        if(result.roles[0] == "ROLE_USER") {
                            PreferenceHelper.saveUserToken(mainActivity, result.token)
                            PreferenceHelper.saveUserId(mainActivity, result.email)
                            PreferenceHelper.saveUserNickname(mainActivity, result.username)
                            PreferenceHelper.saveUserType(mainActivity, "email")
                        }
                        */
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: SignInResponse? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")
                        Toast.makeText(fragment.requireContext(), "아이디 혹은 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString())
                    Toast.makeText(fragment.requireContext(), "네트워크를 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
