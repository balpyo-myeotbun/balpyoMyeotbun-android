package com.project.balpyo.TimeCalculator

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.request.EditScriptRequestWithCalc
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.response.SpeechMark
import com.project.balpyo.databinding.FragmentTimeCalculatorResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimeCalculatorResultFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorResultBinding
    lateinit var mainActivity: MainActivity

    private var totalDuration: Long = 0
    var basetime = 0L

    var editable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTimeCalculatorResultBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()

        binding.run {
            val breakTimeToRealWord = breakTimeToRealWord(MyApplication.speechMarks)
            Log.d("breakTimeToRealWord", breakTimeToRealWord.toString())
            val endByteToRealEndByte = endByteToRealEndByte(breakTimeToRealWord)
            Log.d("endByteToRealEndByte", endByteToRealEndByte.toString())
            val generateRealSpeechMark = generateRealSpeechMark(MyApplication.timeCalculatorScript, endByteToRealEndByte)
            Log.d("generateRealSpeechMark", generateRealSpeechMark.toString())

            Log.d("##", "setting time : ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초")
            textViewCalculateTime.text = "발표시간은 ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초에요!"
            editTextScript.setText(MyApplication.timeCalculatorScript)
            basetime = (MyApplication.timeCalculatorTime * 1000)

            binding.editTextScript.isFocusableInTouchMode = false

            if(MyApplication.calculatedTime == MyApplication.timeCalculatorTime) {
                // 목표 발표 시간을 맞춘 경우
//                textViewGoalTime.text = "목표 발표 시간을 맞췄어요!"
//                textViewGoalTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                layoutTimeNotMatch.visibility = View.GONE
            }
            else if(MyApplication.calculatedTime < MyApplication.timeCalculatorTime) {
                // 목표 발표 시간보다 부족한 경우
                val totalRemainTime = MyApplication.timeCalculatorTime - MyApplication.calculatedTime
                layoutTimeNotMatch.visibility = View.VISIBLE
//                textViewGoalTime.text = "목표 발표 시간보다"
//                textViewGoalTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
                textViewTimeNotMatch.text = "${totalRemainTime/60}분 ${totalRemainTime%60}초 부족"
            }
            else {
                // 목표 발표 시간을 초과한 경우

                val totalRemainTime = MyApplication.calculatedTime - MyApplication.timeCalculatorTime

                val index = findIndex(generateRealSpeechMark, basetime.toInt())
                val highlightScript = highlightOverString(MyApplication.timeCalculatorScript, index)
                binding.editTextScript.setText(highlightScript)

                layoutTimeNotMatch.visibility = View.VISIBLE
//                textViewGoalTime.text = "목표 발표 시간보다"
//                textViewGoalTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
                textViewTimeNotMatch.text = "${totalRemainTime/60}분 ${totalRemainTime%60}초 초과"
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
                    // TODO: 새로 생성이 아니라 수정하기로 변경해야할 것 같아요!
                }
                editable = !editable
            }
        }

        return binding.root
    }

    //Break Time에서 "---ms"를 분리하여 반환
    private fun extractBreakTime(breakMarkup: String): String {
        val regex = "<break time=\"([0-9]+ms)\"/>".toRegex()
        val matchResult = regex.find(breakMarkup)
        val breakTime = matchResult?.groups?.get(1)?.value
        return breakTime ?: ""
    }

    //기존 단어로 치환하기 위한 맵
    private val breakTimeMap = mapOf(
        "601ms" to listOf("."),
        "400ms" to listOf(","),
        "600ms" to listOf("!"),
        "801ms" to listOf("?"),
        "800ms" to listOf("\n"),
    )
    //스피치 마크의 <break time="---ms"/>를 기존의 단어로 변환한 스피치마크 반환
    private fun breakTimeToRealWord(speechMarks : List<SpeechMark>) : List<SpeechMark>{
        val speechMark = mutableListOf<SpeechMark>()
        val firstByte = speechMarks[0].start
        speechMarks.forEach { mark ->
            val breakTime = extractBreakTime(mark.value)
            if(breakTime != "") {
                val realWord = breakTimeMap[breakTime]?.firstOrNull() ?: ""
                speechMark.add(SpeechMark(mark.start - firstByte, mark.end - firstByte , mark.time, mark.type, realWord))
            }
            else if(mark.value == "<amazon:breath/>"){
                speechMark.add(SpeechMark(mark.start - firstByte, mark.end - firstByte , mark.time, mark.type, "\n\n"))
            }
            else {
                speechMark.add(SpeechMark(mark.start - firstByte, mark.end - firstByte, mark.time, mark.type, mark.value))
            }
        }
        return speechMark
    }
    private fun endByteToRealEndByte(speechMarks : List<SpeechMark>): List<SpeechMark>{
        val speechMark = mutableListOf<SpeechMark>()
        var byteOffset = 0
        for (mark in speechMarks) {
            val byte = mark.value.toByteArray(Charsets.UTF_8).size
            val originalByte = mark.end - mark.start
            byteOffset += byte - originalByte
            val start = mark.start + byteOffset
            val end = mark.end+ byteOffset
            val time = mark.time
            speechMark.add(SpeechMark(start, end, time, mark.type, mark.value))
        }
        return speechMark
    }
    private fun byteIndexToCharIndex(text: String, byteIndex: Int): Int {
        val bytes = text.toByteArray(Charsets.UTF_8)
        val subBytes = bytes.sliceArray(0 until byteIndex)
        return subBytes.toString(Charsets.UTF_8).length
    }
    private fun generateRealSpeechMark(originalText: String, speechMarks : List<SpeechMark>) : List<SpeechMark> {
        val speechMark = mutableListOf<SpeechMark>()
        for (mark in speechMarks) {
            val start = byteIndexToCharIndex(originalText, mark.start)
            val end = byteIndexToCharIndex(originalText, mark.end)
            speechMark.add(SpeechMark(start, end, mark.time, mark.type, mark.value))
        }
        return speechMark
    }
    private fun findIndex(speechMarks: List<SpeechMark>, time: Int): Int {
        Log.d("Time", time.toString())
        var index = speechMarks.last().end
        var previousMark: SpeechMark? = null

        for (mark in speechMarks) {
            if (mark.time > time) {
                index = previousMark?.end ?: mark.end
                Log.d("Index", index.toString())
                break
            } else {
                previousMark = mark
            }
        }
        return index
    }

    private fun highlightOverString(originalText: String, fromIndex: Int): SpannableString {
        val spannableString = SpannableString(originalText)
        val length = originalText.length
        spannableString.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.NORMAL), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(requireContext().getColor(R.color.primary)), fromIndex, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
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
    //TODO: api 변경됨 추가 수정 필요
    fun editTime() {
        val action = TimeCalculatorResultFragmentDirections.actionTimeCalculatorResultFragmentToLoadingFragment(
            toolbarTitle = "시간 계산",
            comment = "발표 시간을 계산하고 있어요"
        )
        findNavController().navigate(action)
        val apiClient = ApiClient(mainActivity)

        val request = EditScriptRequestWithCalc(
            MyApplication.timeCalculatorScript,
            MyApplication.timeCalculatorTitle,
            MyApplication.timeCalculatorSpeed.toInt()
        )
        apiClient.apiService.editAndCalc(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)!!}",
            //TODO: ID 추가하기
            1,
            request
        ).enqueue(object :
            Callback<BaseDto> {
            override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: BaseDto? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    convertMsToMinutesSeconds(((result?.playTime?.toLong()) ?: 1) *1000)

                    Log.d("##", "duration : ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초")

                    findNavController().navigate(R.id.timeCalculatorResultFragment)

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
    fun generateAudio() {
        val action = TimeCalculatorResultFragmentDirections.actionTimeCalculatorResultFragmentToLoadingFragment(
            toolbarTitle = "시간 계산",
            comment = "발표 시간을 계산하고 있어요"
        )
        findNavController().navigate(action)
        val apiClient = ApiClient(mainActivity)

        val request = GenerateAudioRequest(
            MyApplication.timeCalculatorScript,
            MyApplication.timeCalculatorSpeed.toInt()
        )
        apiClient.apiService.generateAudio(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)!!}",
            "audio/mp3",
            request
        ).enqueue(object :
            Callback<BaseDto> {
            override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: BaseDto? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    convertMsToMinutesSeconds(((result?.playTime?.toLong()) ?: 1) *1000)

                    Log.d("##", "duration : ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초")

                    findNavController().navigate(R.id.timeCalculatorResultFragment)

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
    //밀리 초를 mm:ss로 변환
    fun convertMsToMinutesSeconds(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        MyApplication.calculatedTimeMinute = minutes.toInt()
        MyApplication.calculatedTimeSecond = seconds.toInt()

        MyApplication.calculatedTime = totalSeconds

        return String.format("%02d:%02d", minutes, seconds)
    }
}