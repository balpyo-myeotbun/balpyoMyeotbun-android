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
import com.project.balpyo.api.response.GenerateAudioResponse
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
    var basetime = 0L

    var editable = false

    val restDuration = 500L
    val normalCharacterDuration = 150L
    val endAwsomeDuration = 600L
    val questionDuration = 800L
    val enterDuration = 300L

    var startIndex = 0
    var endIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimeCalculatorResultBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()

        binding.run {
            Log.d("##", "setting time : ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초")
            textViewCalculateTime.text = "발표시간은 ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초에요!"
            editTextScript.setText(MyApplication.timeCalculatorScript)
            basetime = (MyApplication.timeCalculatorTime * 1000).toLong()

            binding.editTextScript.isFocusableInTouchMode = false

            if(MyApplication.calculatedTime == MyApplication.timeCalculatorTime) {
                // 목표 발표 시간을 맞춘 경우
                textViewGoalTime.text = "목표 발표 시간을 맞췄어요!"
                textViewGoalTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                layoutTimeNotMatch.visibility = View.GONE
            }
            else if(MyApplication.calculatedTime < MyApplication.timeCalculatorTime) {
                // 목표 발표 시간보다 부족한 경우
                var totalRemainTime = MyApplication.timeCalculatorTime - MyApplication.calculatedTime
                layoutTimeNotMatch.visibility = View.VISIBLE
                textViewGoalTime.text = "목표 발표 시간보다"
                textViewGoalTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
                textViewTimeNotMatch.text = "${totalRemainTime/60}분 ${totalRemainTime%60}초 부족"
            }
            else {
                // 목표 발표 시간을 초과한 경우

                var totalRemainTime = MyApplication.calculatedTime - MyApplication.timeCalculatorTime

                calculateOverTime()

                layoutTimeNotMatch.visibility = View.VISIBLE
                textViewGoalTime.text = "목표 발표 시간보다"
                textViewGoalTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
                textViewTimeNotMatch.text = "${totalRemainTime/60}분 ${totalRemainTime%60}초 초과"
            }

            buttonStore.setOnClickListener {
                storeScript()
            }

            buttonEdit.setOnClickListener {
                if(buttonEdit.text == "대본 수정하고 다시 계산하기"){
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray2)
                    buttonEdit.text = "다시 계산하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                    editTextScript.isFocusableInTouchMode = true
                }
                else if(buttonEdit.text == "다시 계산하기"){
                    MyApplication.timeCalculatorScript = binding.editTextScript.text.toString()
                    generateAudio(mainActivity)
                }
                editable = !editable
            }
        }

        return binding.root
    }

    fun calculateOverTime() {
        var currentDuration = 0L // 현재 누적된 시간을 추적하기 위한 변수입니다.

        totalDuration = MyApplication.timeCalculatorScript.foldIndexed(0L) { index, acc, c ->
            currentDuration += when (c) {
                ',' -> restDuration
                '.', '!' -> endAwsomeDuration
                '?' -> questionDuration
                '\n' -> enterDuration
                else -> normalCharacterDuration
            }
            if (currentDuration > basetime) {
                if(startIndex == 0) {

                    Log.d("##", "basetime : ${basetime}")
                    Log.d("##", "current duration : ${currentDuration}")

                    Log.d("발표몇분","Character '$c' which is index $index reached threshold duration.")

                    startIndex = index
                    endIndex = MyApplication.timeCalculatorScript.length

                    var fullText = MyApplication.timeCalculatorScript

                    val spannableString = SpannableString(fullText)

                    // 시작 인덱스와 끝 인덱스 사이의 텍스트에 다른 색상 적용
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.parseColor("#EB2A63")), // 색상 설정
                        index, // 시작 인덱스
                        endIndex, // 끝 인덱스 (exclusive)
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE // 스타일 적용 범위 설정
                    )

                    Log.d("##", "index : ${index}, ${endIndex}")

                    binding.editTextScript.setText(spannableString)
                }
            }
            acc + currentDuration // 누적된 시간을 반환합니다.
        }
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "시간 계산"
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack(R.id.timeCalculatorSpeedFragment, false)
            }
            toolbar.buttonClose.setOnClickListener {
                findNavController().popBackStack(R.id.homeFragment, false)
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

                    findNavController().popBackStack(R.id.homeFragment, false)

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
    fun generateAudio(requireActivity: FragmentActivity) {
        val action = TimeCalculatorResultFragmentDirections.actionTimeCalculatorResultFragmentToLoadingFragment(
            toolbarTitle = "시간 계산",
            comment = "발표 시간을 계산하고 있어요"
        )
        findNavController().navigate(action)
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        val request = GenerateAudioRequest(MyApplication.timeCalculatorScript, MyApplication.timeCalculatorSpeed.toInt(), "1234")
        apiClient.apiService.generateAudio("audio/mp3", request)?.enqueue(object :
            Callback<GenerateAudioResponse> {
            override fun onResponse(call: Call<GenerateAudioResponse>, response: Response<GenerateAudioResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: GenerateAudioResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    convertMsToMinutesSeconds((result!!.playTime.toLong())*1000)

                    Log.d("##", "duration : ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초")

                    findNavController().navigate(R.id.timeCalculatorResultFragment)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: GenerateAudioResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<GenerateAudioResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }
    //밀리 초를 mm:ss로 변환
    fun convertMsToMinutesSeconds(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        MyApplication.calculatedTimeMinute = minutes.toInt()
        MyApplication.calculatedTimeSecond = seconds.toInt()

        MyApplication.calculatedTime = totalSeconds.toLong()

        return String.format("%02d:%02d", minutes, seconds)
    }
}