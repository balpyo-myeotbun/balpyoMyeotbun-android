package com.project.balpyo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.response.GenerateUidResponse
import com.project.balpyo.api.response.VerifyUidResponse
import com.project.balpyo.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    private lateinit var googleLoginHelper: GoogleSignInHelper

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

        googleLoginHelper = GoogleSignInHelper(requireContext())

        binding.tvLoginEmail.setOnClickListener {
            googleLoginHelper.requestGoogleLogin(
                onSuccess = { message ->
                    Log.d("GoogleLogin", message)
                    val action = LoginFragmentDirections.actionLoginFragmentToSignUpTermsFragment(
                        type = "google"
                    )
                    findNavController().navigate(action)
            },
                onFailure = { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                })
        }
        binding.run {
            btnLoginEmail.setOnClickListener {

            }
            btnLoginKakao.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToSignUpTermsFragment(
                    type = "kakao"
                )
                findNavController().navigate(action)
            }


            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    when {
                        error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                            Toast.makeText(requireContext(), "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT)
                                .show()
                        }

                        error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                            Toast.makeText(requireContext(), "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                        }

                        error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                            Toast.makeText(
                                requireContext(),
                                "인증 수단이 유효하지 않아 인증할 수 없는 상태",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                            Toast.makeText(requireContext(), "요청 파라미터 오류", Toast.LENGTH_SHORT)
                                .show()
                        }

                        error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                            Toast.makeText(requireContext(), "유효하지 않은 scope ID", Toast.LENGTH_SHORT)
                                .show()
                        }

                        error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                            Toast.makeText(
                                requireContext(),
                                "설정이 올바르지 않음(android key hash)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        error.toString() == AuthErrorCause.ServerError.toString() -> {
                            Toast.makeText(requireContext(), "서버 내부 에러", Toast.LENGTH_SHORT).show()
                        }

                        error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                            Toast.makeText(requireContext(), "앱이 요청 권한이 없음", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> { // Unknown
                            Toast.makeText(requireContext(), "기타 에러", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else if (token != null) {
                    Log.d("token", token.accessToken)
                    //PreferenceHelper.saveUserToken(requireContext(), token.accessToken)
                    //PreferenceHelper.saveUserType(requireContext(), "kakao")
                    val action = LoginFragmentDirections.actionLoginFragmentToSignUpTermsFragment(
                        type = "kakao"
                    )
                    findNavController().navigate(action)
                }
            }
            btnLoginKakao.setOnClickListener {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                    UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(
                        requireContext(),
                        callback = callback
                    )
                }
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