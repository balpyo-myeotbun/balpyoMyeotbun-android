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
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.request.StoreScriptRequest
import com.project.balpyo.api.response.SpeechMark
import com.project.balpyo.api.response.StoreScriptResponse
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
    ): View? {

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

                val index = findIndex(generateRealSpeechMark, basetime.toInt())
                val highlightScript = highlightOverString(MyApplication.timeCalculatorScript, index)
                binding.editTextScript.setText(highlightScript)

                layoutTimeNotMatch.visibility = View.VISIBLE
                textViewGoalTime.text = "목표 발표 시간보다"
                textViewGoalTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
                textViewTimeNotMatch.text = "${totalRemainTime/60}분 ${totalRemainTime%60}초 초과"
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
    fun byteIndexToCharIndex(text: String, byteIndex: Int): Int {
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
    fun findIndex(speechMarks: List<SpeechMark>, time: Int): Int {
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

    fun highlightOverString(originalText: String, fromIndex: Int): SpannableString {
        val spannableString = SpannableString(originalText)
        val length = originalText.length
        spannableString.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.NORMAL), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(requireContext().getColor(R.color.primary)), fromIndex, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    /*fun calculateOverTime() {
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
    }*/

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
                /*val navController = findNavController()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestination, true) // 스택의 처음부터 현재 위치까지 모두 팝
                    .build()

                navController.navigate(R.id.homeFragment, null, navOptions)*/
                findNavController().navigate(R.id.homeFragment)
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

                    /*val navController = findNavController()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(navController.graph.startDestination, true) // 스택의 처음부터 현재 위치까지 모두 팝
                        .build()

                    navController.navigate(R.id.homeFragment, null, navOptions)*/
                    findNavController().navigate(R.id.homeFragment)

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