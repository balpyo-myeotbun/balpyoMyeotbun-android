package com.project.balpyo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.FlowControllerPreviewFragmentDirections
import com.project.balpyo.Script.ScriptTitleFragment
import com.project.balpyo.TimeCalculator.TimeCalculatorScriptFragment
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.GenerateScriptRequest
import com.project.balpyo.api.response.GenerateScriptResponse
import com.project.balpyo.api.response.GenerateUidResponse
import com.project.balpyo.api.response.VerifyUidResponse
import com.project.balpyo.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity
        mainActivity.setTransparentStatusBar()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            btnLoginUid.setOnClickListener {
                verifyUid()
            }
            btnLoginEmail.setOnClickListener {
                findNavController().navigate(R.id.emailLoginFragment)
            }
            btnLoginKakao.setOnClickListener{
                val action = LoginFragmentDirections.actionLoginFragmentToSignUpTermsFragment(
                    isKaKao = true
                )
                findNavController().navigate(action)
            }
        }

        return binding.root
    }

    fun generateUid() {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.generateUid()?.enqueue(object :
            Callback<GenerateUidResponse> {
            override fun onResponse(call: Call<GenerateUidResponse>, response: Response<GenerateUidResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: GenerateUidResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    tokenManager.saveUid("${result?.result?.uid}")
                    Log.d("발표몇분", "${tokenManager.getUid()}")

                    findNavController().navigate(R.id.homeFragment)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: GenerateUidResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<GenerateUidResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    fun verifyUid() {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.verifyUid("${tokenManager.getUid()}")?.enqueue(object :
            Callback<VerifyUidResponse> {
            override fun onResponse(call: Call<VerifyUidResponse>, response: Response<VerifyUidResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: VerifyUidResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    if(result?.result?.verified == false) {
                        generateUid()
                    } else {
                        Log.d("발표몇분", "${tokenManager.getUid()}")

                        findNavController().navigate(R.id.homeFragment)
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: VerifyUidResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    generateUid()
                }
            }

            override fun onFailure(call: Call<VerifyUidResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    override fun onStop() {
        super.onStop()
        mainActivity.resetStatusBar()
    }
}