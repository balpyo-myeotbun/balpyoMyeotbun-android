package com.project.balpyo.TimeCalculator

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.request.StoreScriptRequest
import com.project.balpyo.api.response.StoreScriptResponse
import com.project.balpyo.databinding.FragmentTimeCalculatorResultBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class TimeCalculatorResultFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorResultBinding
    lateinit var mainActivity: MainActivity

    private var totalDuration: Long = 0

    var editable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimeCalculatorResultBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()

        binding.run {
            textViewGoalTime.text = "발표시간은 ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초에요!"
            editTextScript.setText(MyApplication.timeCalculatorScript)

            binding.editTextScript.isFocusableInTouchMode = false

            if(MyApplication.calculatedTime == MyApplication.timeCalculatorTime) {
                textViewSuccess.text = "목표 발표 시간을 맞췄어요!"
            } else if(MyApplication.calculatedTime < MyApplication.timeCalculatorTime) {
                var totalRemainTime = MyApplication.timeCalculatorTime - MyApplication.calculatedTime
                textViewSuccess.text = "목표 발표 시간보다\n${totalRemainTime/60}분 ${totalRemainTime%60}초 부족해요"
            } else {
                var totalRemainTime = MyApplication.calculatedTime - MyApplication.timeCalculatorTime

                var fullText = MyApplication.timeCalculatorScript

                val spannableString = SpannableString(fullText)

                // 시작 인덱스와 끝 인덱스 사이의 텍스트에 다른 색상 적용
                val startIndex = MyApplication.timeCalculatorScript.length - 0
                val endIndex = MyApplication.timeCalculatorScript.length
                spannableString.setSpan(
                    ForegroundColorSpan(Color.parseColor("#EB2A63")), // 색상 설정
                    startIndex, // 시작 인덱스
                    endIndex, // 끝 인덱스 (exclusive)
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE // 스타일 적용 범위 설정
                )

                editTextScript.setText(spannableString)

                textViewSuccess.text = "목표 발표 시간보다\n${totalRemainTime/60}분 ${totalRemainTime%60}초 초과해요"
            }

            buttonStore.setOnClickListener {
                storeScript()
            }

            buttonEdit.setOnClickListener {
                if(editable) {
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
                    buttonEdit.text = "대본 수정하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray3))
                    editTextScript.isFocusableInTouchMode = false
                } else {
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray2)
                    buttonEdit.text = "대본 수정 완료"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                    editTextScript.isFocusableInTouchMode = true
                }
                editable = !editable
            }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
            toolbar.buttonClose.setOnClickListener {
                // 닫기 버튼 클릭시 동작
                val navController = findNavController()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestination, true) // 스택의 처음부터 현재 위치까지 모두 팝
                    .build()

                navController.navigate(R.id.homeFragment, null, navOptions)
            }
        }
    }

    fun storeScript() {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        var inputScriptInfo = StoreScriptRequest(binding.editTextScript.text.toString(), "", MyApplication.timeCalculatorTitle, MyApplication.timeCalculatorTime)
        Log.d("##", "script info : ${inputScriptInfo}")

        apiClient.apiService.storeScript("${tokenManager.getUid()}",inputScriptInfo)?.enqueue(object :
            Callback<StoreScriptResponse> {
            override fun onResponse(call: Call<StoreScriptResponse>, response: Response<StoreScriptResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: StoreScriptResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    val navController = findNavController()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(navController.graph.startDestination, true) // 스택의 처음부터 현재 위치까지 모두 팝
                        .build()

                    navController.navigate(R.id.homeFragment, null, navOptions)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: StoreScriptResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<StoreScriptResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }


}