package com.project.balpyo.MyPage

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.MyApplication.Companion.mainActivity
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.EmptyDto
import com.project.balpyo.databinding.FragmentMyPageChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageChangePasswordFragment : Fragment() {

    lateinit var binding: FragmentMyPageChangePasswordBinding
    lateinit var mainActivity: MainActivity

    var regularExpression1 = false
    var regularExpression2 = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyPageChangePasswordBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()
        observeKeyboardState()

        binding.run {

            editTextPasswordCheck.visibility = View.INVISIBLE
            textViewMypagePasswordCheckTitle.visibility = View.INVISIBLE

            editTextPassword.addTextChangedListener {
                if(editTextPassword.text.isNotEmpty()){
                    if(editTextPasswordCheck.text.isNotEmpty()) {
                        if(editTextPasswordCheck.text.toString() == editTextPassword.text.toString()) {
                            buttonNext.isEnabled = true
                            buttonNextKeyboard.isEnabled = true
                        } else {
                            buttonNext.isEnabled = false
                            buttonNextKeyboard.isEnabled = false
                        }
                    } else {
                        isValidPassword()
                    }
                }
                else{
                    buttonNext.isEnabled = false
                    buttonNextKeyboard.isEnabled = false
                }
            }

            editTextPasswordCheck.addTextChangedListener {
                if(editTextPasswordCheck.text.isNotEmpty()) {
                    if(editTextPasswordCheck.text.toString() == editTextPassword.text.toString()) {
                        buttonNext.isEnabled = true
                        buttonNextKeyboard.isEnabled = true
                    } else {
                        buttonNext.isEnabled = false
                        buttonNextKeyboard.isEnabled = false
                    }
                }
                else{
                    buttonNext.isEnabled = false
                    buttonNextKeyboard.isEnabled = false
                }
            }

            buttonNext.setOnClickListener {
                if(editTextPasswordCheck.text.isNotEmpty()) {
                    // 비밀번호 변경 API 요청
                    changePassword(MyApplication.currentPassword, editTextPasswordCheck.text.toString())
                } else {
                    buttonNext.isEnabled = false
                    textViewMypagePasswordCheckTitle.visibility = View.VISIBLE
                    editTextPasswordCheck.visibility = View.VISIBLE
                }
            }

            buttonNextKeyboard.setOnClickListener {
                if(editTextPasswordCheck.text.isNotEmpty()) {
                    // 비밀번호 변경 API 요청
                    changePassword(MyApplication.currentPassword, editTextPasswordCheck.text.toString())
                } else {
                    buttonNext.isEnabled = false
                    textViewMypagePasswordCheckTitle.visibility = View.VISIBLE
                    editTextPasswordCheck.visibility = View.VISIBLE
                }
            }
        }

        return binding.root
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        val apiClient = ApiClient(mainActivity)

        apiClient.apiService.changePassword(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)!!}",
            currentPassword,
            newPassword
        ).enqueue(object :
            Callback<EmptyDto> {
            override fun onResponse(call: Call<EmptyDto>, response: Response<EmptyDto>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: EmptyDto? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    findNavController().navigate(R.id.myPageChangePasswordCompleteFragment)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: EmptyDto? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<EmptyDto>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    fun checkRegularExpression(password: String): Boolean {
        val hasLetter = password.contains(Regex("[a-zA-Z]"))
        val hasDigit = password.contains(Regex("[0-9]"))
        val hasSpecialChar = password.contains(Regex("[^a-zA-Z0-9 ]"))

        val combinationCount = listOf(hasLetter, hasDigit, hasSpecialChar).count { it }

        return combinationCount >= 2
    }

    fun isValidPassword() {
        binding.run {
            if(editTextPassword.length() >= 10) {
                regularExpression1 = true
                textViewRegularExpression1Check.setTextColor(resources.getColor(R.color.black))
                imageViewRegularExpression1Check.setImageResource(R.drawable.ic_check_selected)
            } else {
                regularExpression1 = false
                textViewRegularExpression1Check.setTextColor(resources.getColor(R.color.disabled))
                imageViewRegularExpression1Check.setImageResource(R.drawable.ic_uncheck)
            }

            if(checkRegularExpression(editTextPassword.text.toString())) {
                regularExpression2 = true
                textViewRegularExpression2Check.setTextColor(resources.getColor(R.color.black))
                imageViewRegularExpression2Check.setImageResource(R.drawable.ic_check_selected)
            } else {
                regularExpression2 = false
                textViewRegularExpression2Check.setTextColor(resources.getColor(R.color.disabled))
                imageViewRegularExpression2Check.setImageResource(R.drawable.ic_uncheck)
            }

            if(regularExpression1 && regularExpression2) {
                buttonNext.isEnabled = true
                buttonNextKeyboard.isEnabled = true
            } else {
                buttonNext.isEnabled = false
                buttonNextKeyboard.isEnabled = false
            }
        }
    }

    fun initToolBar() {
        binding.run {
            toolbar.imageViewButtonBack.visibility = View.VISIBLE
            toolbar.imageViewButtonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "2/2"
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "비밀번호 변경하기"
            toolbar.imageViewButtonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun observeKeyboardState() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            var originHeight = -1
            if ( binding.root.height > originHeight) {
                originHeight =  binding.root.height
            }

            val visibleFrameSize = Rect()
            binding.root.getWindowVisibleDisplayFrame(visibleFrameSize)

            val visibleFrameHeight = visibleFrameSize.bottom - visibleFrameSize.top
            val keyboardHeight = originHeight - visibleFrameHeight

            if (keyboardHeight > visibleFrameHeight * 0.15) {
                // 키보드가 올라옴
                binding.buttonNextKeyboard.visibility = View.VISIBLE
                binding.buttonNext.visibility = View.GONE
                binding.buttonNextKeyboard.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.buttonNextKeyboard.visibility = View.GONE
                binding.buttonNext.visibility = View.VISIBLE
            }
        }
    }
}