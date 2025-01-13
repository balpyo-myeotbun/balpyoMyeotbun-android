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
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.request.EditScriptRequestWithCalc
import com.project.balpyo.databinding.FragmentMyPageCurrentPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageCurrentPasswordFragment : Fragment() {

    lateinit var binding: FragmentMyPageCurrentPasswordBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyPageCurrentPasswordBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()
        observeKeyboardState()

        binding.run {
            editTextCurrentPassword.addTextChangedListener {
                if(editTextCurrentPassword.text.isNotEmpty()){
                    buttonNext.isEnabled = true
                    buttonNextKeyboard.isEnabled = true
                }
                else{
                    buttonNext.isEnabled = false
                    buttonNextKeyboard.isEnabled = false
                }
            }

            buttonNext.setOnClickListener {
                checkPassword(editTextCurrentPassword.text.toString())
            }

            buttonNextKeyboard.setOnClickListener {
                checkPassword(editTextCurrentPassword.text.toString())
            }
        }

        return binding.root
    }

    fun checkPassword(currentPassword: String) {
        val apiClient = ApiClient(mainActivity)

        apiClient.apiService.checkPassword(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)!!}",
            currentPassword
        ).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: Boolean? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    findNavController().navigate(R.id.myPageChangePasswordFragment)
                    MyApplication.currentPassword = currentPassword
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: Boolean? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    fun initToolBar() {
        binding.run {
            toolbar.imageViewButtonBack.visibility = View.VISIBLE
            toolbar.imageViewButtonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "1/2"
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