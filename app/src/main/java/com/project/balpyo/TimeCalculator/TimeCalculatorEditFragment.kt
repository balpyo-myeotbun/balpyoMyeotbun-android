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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.BottomSheet.FlowControllerEditBottomSheetFragment
import com.project.balpyo.FlowController.BottomSheet.FlowControllerEditBottomSheetListener
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ViewModel.ScriptDetailViewModel
import com.project.balpyo.Storage.LoadScriptBottomSheet.LoadScriptBottomSheetFragment
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.request.EditScriptRequestWithCalc
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.response.SpeechMark
import com.project.balpyo.databinding.FragmentTimeCalculatorEditBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TimeCalculatorEditFragment : Fragment(), FlowControllerEditBottomSheetListener {

    lateinit var binding: FragmentTimeCalculatorEditBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: ScriptDetailViewModel

    var basetime = 0L

    var generatedScriptResult = BaseDto(
        id = null,
        content = null,
        title = null,
        secTime = null,
        voiceFilePath = null,
        isGenerating = null,
        playTime = null,
        originalScript = null,
        speed = null,
        useAi = null,
        tags = null,
        topic = null,
        keywords = null,
        fcmToken = null,
        speechMark = null
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimeCalculatorEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(requireActivity())[ScriptDetailViewModel::class.java]

        viewModel.run {
            scriptResult.observe(mainActivity) {
                generatedScriptResult = it
            }
        }

        initView()
        checkTime()

        return binding.root
    }

    private fun checkTime() {
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
        }
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

    fun initView() {
        binding.run {
            textViewLoading.visibility = View.INVISIBLE
            toolbar.run {
                imageViewButtonBack.visibility = View.VISIBLE
                imageViewButtonClose.visibility = View.INVISIBLE
                textViewPage.visibility = View.INVISIBLE
                imageViewButtonEditMenu.visibility = View.VISIBLE
                textViewTitle.visibility = View.VISIBLE
                textViewTitle.text = "${generatedScriptResult.title}"
                imageViewButtonBack.setOnClickListener {
                    // 뒤로가기 버튼 클릭시 동작
                    findNavController().popBackStack()
                }
                imageViewButtonEditMenu.setOnClickListener {
                    val bottomSheetFragment = FlowControllerEditBottomSheetFragment()
                    bottomSheetFragment.show(
                        mainActivity.supportFragmentManager,
                        bottomSheetFragment.tag
                    )
                }
                textViewButtonStore.setOnClickListener {
                    textViewButtonStore.visibility = View.INVISIBLE
                    textViewLoading.visibility = View.VISIBLE
                    editTime()
                }
            }
        }
    }

    fun editTime() {
        val apiClient = ApiClient(mainActivity)

        val request = EditScriptRequestWithCalc(
            binding.toolbar.textViewTitle.text.toString(),
            binding.editTextScript.text.toString(),
            generatedScriptResult.speed!!
        )
        apiClient.apiService.editAndCalc(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)!!}",
            generatedScriptResult.id!!,
            request
        ).enqueue(object :
            Callback<BaseDto> {
            override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: BaseDto? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    convertMsToMinutesSeconds((result?.playTime?.toLong())?.times(1000) ?: 0)
                    MyApplication.speechMarks = result?.speechMark ?: emptyList()
                    Log.d("##", "duration : ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초")

                    checkTime()

                    binding.run {
                        textViewLoading.visibility = View.INVISIBLE
                        editTextScript.isFocusableInTouchMode = false
                        toolbar.run {
                            imageViewButtonEditMenu.visibility = View.VISIBLE
                        }
                    }


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

    override fun onItemSelected(position: Int) {
        if(position == 0) {
            // position == 0 : 삭제

        } else {
            // position == 1 : 수정
            binding.run {
                editTextScript.isFocusableInTouchMode = true
                toolbar.run {
                    imageViewButtonEditMenu.visibility = View.INVISIBLE
                    textViewButtonStore.visibility = View.VISIBLE
                }
            }
        }
    }
}