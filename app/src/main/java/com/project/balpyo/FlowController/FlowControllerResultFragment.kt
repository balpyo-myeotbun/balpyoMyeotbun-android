package com.project.balpyo.FlowController

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.gson.annotations.SerializedName
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.response.GenerateAudioResponse
import com.project.balpyo.api.response.SpeechMark
import com.project.balpyo.databinding.FragmentFlowControllerResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FlowControllerResultFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    private lateinit var scriptTextView: TextView
    private lateinit var playButton: ImageView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler
    private var speechMarks: List<SpeechMark> = listOf()
    private var realSpeechMark : List<SpeechMark>  = listOf()
    private lateinit var binding: FragmentFlowControllerResultBinding
    private var isPlaying = false
    private lateinit var viewDataBinding: FragmentFlowControllerResultBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        binding = FragmentFlowControllerResultBinding.inflate(inflater, container, false)
        scriptTextView = binding.FCRScript
        playButton = binding.PCPlayBtn
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        viewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_flow_controller_result,
            null,
            false
        )
        initToolBar()

        val speed = flowControllerViewModel.getSpeedData().value
        when(speed) {
            -2 -> {binding.FCSpeedCL03.setBackgroundResource(R.drawable.selected_speed)
                binding.FCSpeed03Tv.setTextColor(this.resources.getColor(R.color.primary))
            }
            -1 -> {binding.FCSpeedCL05.setBackgroundResource(R.drawable.selected_speed)
                binding.FCSpeed05Tv.setTextColor(this.resources.getColor(R.color.primary))
            }
            0 -> {binding.FCSpeedCL1.setBackgroundResource(R.drawable.selected_speed)
                binding.FCSpeed1Tv.setTextColor(this.resources.getColor(R.color.primary))
            }
            1 -> {binding.FCSpeedCL15.setBackgroundResource(R.drawable.selected_speed)
                binding.FCSpeed15Tv.setTextColor(this.resources.getColor(R.color.primary))
            }
            2 -> {binding.FCSpeedCL2.setBackgroundResource(R.drawable.selected_speed)
                binding.FCSpeed2Tv.setTextColor(this.resources.getColor(R.color.primary))
            }
            else -> println("none")
        }

        val script = flowControllerViewModel.getCustomScriptData().value.toString()
        scriptTextView.text = script
        speechMarks = flowControllerViewModel.getSpeechMarks().value!!

        val breakTimeToRealWord = breakTimeToRealWord(speechMarks)
        Log.d("breakTimeToRealWord", breakTimeToRealWord.toString())
        val endByteToRealEndByte = endByteToRealEndByte(breakTimeToRealWord)
        Log.d("endByteToRealEndByte", endByteToRealEndByte.toString())
        val generateRealSpeechMark = generateRealSpeechMark(script, endByteToRealEndByte)
        Log.d("generateRealSpeechMark", generateRealSpeechMark.toString())
        realSpeechMark = generateRealSpeechMark

        initializeMediaPlayer()
        initializeSeekBar()
        playButton.setOnClickListener {
            if (isPlaying) {
                pausePlayback()
            } else {
                startPlayback()
            }
        }
        binding.FCSpeedCL03.setOnClickListener {
            flowControllerViewModel.setSpeed(-2)
            generateAudio()
        }
        binding.FCSpeedCL05.setOnClickListener {
            flowControllerViewModel.setSpeed(-1)
            generateAudio()
        }
        binding.FCSpeedCL1.setOnClickListener {
            flowControllerViewModel.setSpeed(0)
            generateAudio()
        }
        binding.FCSpeedCL15.setOnClickListener {
            flowControllerViewModel.setSpeed(1)
            generateAudio()
        }
        binding.FCSpeedCL2.setOnClickListener {
            flowControllerViewModel.setSpeed(2)
            generateAudio()
        }

        binding.PCEditBtn.setOnClickListener {
            findNavController().popBackStack(R.id.flowControllerTitleFragment, false)
        }
        binding.PCStoreBtn.setOnClickListener {
            findNavController().navigate(R.id.storageFragment)
        }

        return binding.root
    }

    //플레이어 초기화
    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(flowControllerViewModel.getAudioUrlData().value!!)
            mediaPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace() // URL이 잘못되었거나, 네트워크 문제 등으로 예외 처리
        }
        mediaPlayer.setOnCompletionListener {
            stopPlayback()
        }
        handler = Handler()
    }

    //시작, 끝 재생 시간 업데이트
    private fun updatePlaybackTime() {
        val currentPosition = mediaPlayer.currentPosition //현재까지의 재생 시간
        val totalTime = mediaPlayer.duration //총 재생 시간
        val remainingTime = totalTime - currentPosition //끝부분 시각

        val startTime = convertMsToMinutesSeconds(currentPosition.toLong())
        val endTime = convertMsToMinutesSeconds(remainingTime.toLong())
        binding.PCStartTimeTextView.text = startTime
        if(endTime == "00:00" || remainingTime == totalTime)
            binding.PCEndTimeTextView.text = endTime
        else
            binding.PCEndTimeTextView.text = "-" + endTime
    }

    // 재생 시간을 mm:ss 형식으로 변환
    fun convertMsToMinutesSeconds(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d", minutes, seconds)
    }

    //seekbar 초기화
    private fun initializeSeekBar() {
        binding.PCTimeBar.max = mediaPlayer.duration
        updatePlaybackTime()
        //seekbar 리스너
        binding.PCTimeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            //seekbar가 변화될때
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                    handler.post(updateSeekBarTask) // 재생 시작과 함께 재생 바 업데이트
                    highlightText(progress) // 해당 시간에 맞는 텍스트 하이라이팅
                }
            }

            //seekbar를 움직이는 동안
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 재생 중지
                mediaPlayer.pause()
                isPlaying = false
                playButton.setImageDrawable(requireActivity().getDrawable(R.drawable.exclude))
            }

            //seekbar 조정이 끝날 시
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 재생 시작
                mediaPlayer.start()
                isPlaying = true
                playButton.setImageDrawable(requireActivity().getDrawable(R.drawable.pause))
                handler.post(updateSeekBarTask) // 재생 바 업데이트
            }
        })
    }

    //재생바를 업데이트하기 위함
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val currentPosition = mediaPlayer.currentPosition
                binding.PCTimeBar.progress = currentPosition
                updatePlaybackTime()
                highlightText(currentPosition) // 현재 재생 위치에 따라 텍스트 하이라이팅
                handler.postDelayed(this, 10) //1ms마다 업데이트
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

    private fun highlightText(progress: Int) {
        /*val zeroSpeechMark = firstStartToZero(speechMarks)
        Log.d("zeroSpeechMark", zeroSpeechMark.toString())
        val breakTimeToOriginalText = breakTimeToOriginalText(zeroSpeechMark, scriptTextView.text.toString())
        Log.d("breakTimeToOriginalText", breakTimeToOriginalText.toString())
        adjustStartEndByteOffset(breakTimeToOriginalText)
        //val indexSpeechMark = byteSpeechMarkToIndexSpeechMark(breakTimeToOriginalText, scriptTextView.text.toString())
        //Log.d("indexSpeechMark", indexSpeechMark.toString())
        val adjustedMarks = adjustSpeechMarksForCustomMarkup(speechMarks, scriptTextView.text.toString())
        adjustStartEndByteOffset(adjustedMarks)
        val indexSpeechMark = byteSpeechMarkToIndexSpeechMark(adjustedMarks, scriptTextView.text.toString())
        Log.d("adjust", indexSpeechMark.toString())*/
        val spannableString = SpannableString(scriptTextView.text)
        synchronized(realSpeechMark) {
            var lastEndIndex = 0
            var highlightedLine = -1

            for (mark in realSpeechMark) {
                if (mark.time <= progress && progress <= mark.time + 1000) {
                    val startIndex = 0
                    val endIndex = mark.end

                    spannableString.setSpan(
                        ForegroundColorSpan(resources.getColor(R.color.primary)),
                        startIndex,
                        endIndex,
                        SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    lastEndIndex = endIndex
                    highlightedLine = scriptTextView.layout.getLineForOffset(endIndex)
                }
            }

            spannableString.setSpan(
                ForegroundColorSpan(Color.BLACK),
                lastEndIndex,
                spannableString.length,
                SpannableString.SPAN_INCLUSIVE_INCLUSIVE
            )

            scriptTextView.text = spannableString
            if (highlightedLine < 4) {
                scrollTextViewToLine(0)
            }
            if (highlightedLine > 4) {
                scrollTextViewToLine(highlightedLine)
            }
        }
    }

    private fun scrollTextViewToLine(line: Int) {
        val layout = scriptTextView.layout
        val yScroll = layout.getLineTop(line) - layout.getLineTop(4)
        binding.scrollView2.smoothScrollTo(0, yScroll)

    }

    private fun updateTextHighlighting(progress: Int) {
        if (isPlaying) {
            highlightText(progress)
        }
    }

    private fun startPlayback() {
        mediaPlayer.start()
        playButton.setImageDrawable(requireActivity().getDrawable(R.drawable.pause)) // Change to pause icon
        isPlaying = true
        handler.post(updateSeekBarTask) // 재생 시작과 함께 재생 바 업데이트 시작
        updateTextHighlighting(mediaPlayer.currentPosition)
    }

    private fun pausePlayback() {
        mediaPlayer.pause()
        playButton.setImageDrawable(requireActivity().getDrawable(R.drawable.exclude)) // Change to play icon
        isPlaying = false
        updateTextHighlighting(mediaPlayer.currentPosition)
    }

    private fun stopPlayback() {
        mediaPlayer.pause()
        mediaPlayer.seekTo(0)
        playButton.setImageDrawable(requireActivity().getDrawable(R.drawable.exclude)) // Change to play icon
        isPlaying = false
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.INVISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = flowControllerViewModel.getTitleData().value
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonClose.setOnClickListener {
                findNavController().popBackStack(R.id.homeFragment, false)
                flowControllerViewModel.initialize()
            }
        }
    }
    fun generateAudio() {
        findNavController().navigate(R.id.loadingFragment)
        var apiClient = ApiClient(mainActivity)

        val request = GenerateAudioRequest(flowControllerViewModel.getCustomScriptData().value.toString(), flowControllerViewModel.getSpeedData().value!!, "1234")
        apiClient.apiService.generateAudio("audio/mp3", request)?.enqueue(object :
            Callback<GenerateAudioResponse> {
            override fun onResponse(call: Call<GenerateAudioResponse>, response: Response<GenerateAudioResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: GenerateAudioResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())
                    flowControllerViewModel.setAudioUrl(result!!.profileUrl)
                    flowControllerViewModel.setSpeechMarks(result.speechMarks)
                    stopPlayback()
                    findNavController().navigate(R.id.flowControllerResultFragment)
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
    //단어 파싱 (한글, 영어, 쉼표, 공백, 기호)
    //한글에 숫자는 붙어도됨
    //숫자와 영어는 떨어지게
    //private fun parsingScript(originalText: String): List<String> {
        // 정규 표현식
    //    val regex = Regex("[가-힣0-9]+|[a-zA-Z]+|[0-9]+|\\s|[.,!?\\[\\](){}<>+\\-*/=~`@#%^&*_|\"'\\\\]")

    //    return regex.findAll(originalText).map { it.value }.toList()
    //}
    /*
    private fun findPunctuationIndex(originalText: String, breakTime: String, startIndex: Int): Int {
        val punctuationList = breakTimeMap[breakTime]
        if (punctuationList != null) {
            for (punctuation in punctuationList) {
                val punctuationIndex = originalText.indexOf(punctuation, startIndex)
                if (punctuationIndex != -1) {
                    return punctuationIndex
                }
            }
        }
        return -1
    }

    private fun firstStartToZero(speechMarks: List<SpeechMark>) : List<SpeechMark> {
        val indexSpeechMark = mutableListOf<SpeechMark>()
        val first = speechMarks[0].start
        speechMarks.forEach { mark ->
            indexSpeechMark.add(SpeechMark((mark.start - first), mark.end-first, mark.time, mark.type, mark.value))
        }
        return indexSpeechMark
    }

    fun breakTimeToOriginalText(speechMarks: List<SpeechMark>, originalText: String): List<SpeechMark> {
        // 정규표현식 패턴을 사용하여 단어, 쉼표, 마침표, 느낌표, 물음표를 기준으로 분리
        val regex = """([\w]+|[.,!?])""".toRegex()

        // 정규표현식을 기준으로 분리하여 리스트 생성
        val splitText = regex.findAll(originalText).map { it.value }.toList()
        Log.d("splitText", splitText.toString())
        // SpeechMark 객체에 값을 할당
        val resultSpeechMarks = mutableListOf<SpeechMark>()
        var currentIndex = 0
        var previousWord = ""

        for (i in splitText.indices) {
            val currentWord = splitText[i]
            if (currentIndex < speechMarks.size) {
                val speechMark = speechMarks[currentIndex]

                // 현재 단어가 공백이 아니고 이전 단어가 공백이 아닌 경우, 이전 단어와 현재 단어를 합쳐서 저장
                if (currentWord != " " && previousWord != " ") {
                    speechMark.value = "$previousWord$currentWord"
                } else {
                    speechMark.value = currentWord
                }
                resultSpeechMarks.add(speechMark)
                currentIndex++
            }
            previousWord = currentWord
        }
        return resultSpeechMarks
    }
    private fun byteSpeechMarkToIndexSpeechMark(speechMark: List<SpeechMark>, originalText: String) : List<SpeechMark>{
        val indexSpeechMark = mutableListOf<SpeechMark>()
        speechMark.forEach { mark ->
            byteOffsetToIndex(originalText, (mark.start*1000).toInt())
            indexSpeechMark.add(SpeechMark(byteOffsetToIndex(originalText, (mark.start*1000).toInt()).toFloat() / 1000, byteOffsetToIndex(originalText, (mark.end*1000).toInt()).toFloat() / 1000, mark.time, mark.type, mark.value))
        }
        return indexSpeechMark
    }
    fun adjustStartEndByteOffset(speechMarks: List<SpeechMark>) {
        var cumulativeOffset = 0

        for (speechMark in speechMarks) {
            val valueByteArray = speechMark.value.toByteArray(Charsets.UTF_8)
            val valueByteLength = valueByteArray.size

            speechMark.start = cumulativeOffset.toFloat() / 1000
            cumulativeOffset += valueByteLength
            speechMark.end = cumulativeOffset.toFloat() /1000
        }
    }

    private fun byteOffsetToIndex(text: String, byteOffset: Int): Int {
        var byteCount = 0
        for (i in text.indices) {
            byteCount += text[i].toString().toByteArray().size
            if (byteCount > byteOffset) {
                return i
            }
        }
        return text.length
    }
    private fun adjustSpeechMarksForCustomMarkup(speechMarks: List<SpeechMark>, originalText: String): List<SpeechMark> {
        val adjustedMarks = mutableListOf<SpeechMark>()
        var adjustmentOffset = 0
        var currentOriginalIndex = 0

        speechMarks.forEach { mark ->
            if (mark.value.contains("<break")) {
                // 커스텀 마크업을 구두점으로 변환
                val breakTime = extractBreakTime(mark.value)
                Log.d("FlowControllerResultFragment", "Break time found: $breakTime")
                if (breakTimeMap.containsKey(breakTime)) {
                    // Find the correct punctuation in the original text
                    val punctuationIndex = findPunctuationIndex(originalText, breakTime, currentOriginalIndex)
                    Log.d("FlowControllerResultFragment", "Punctuation index: $punctuationIndex")
                    if (punctuationIndex != -1) {
                        adjustmentOffset += 1 - mark.value.length
                        currentOriginalIndex = punctuationIndex + 1
                        val displayStart = findStartIndex(punctuationIndex)
                        val displayEnd = findEndIndex(punctuationIndex + 1)
                        adjustedMarks.add(
                            SpeechMark(
                                displayStart.toFloat() / 1000,
                                displayEnd.toFloat() / 1000,
                                mark.time,
                                mark.type,
                                originalText.substring(punctuationIndex, punctuationIndex + 1)
                            )
                        )
                    }
                } else {
                    Log.e("FlowControllerResultFragment", "Break time not found in map: $breakTime")
                }
            } else {
                // 원본 텍스트 인덱스를 표시할 텍스트 인덱스로 변환
                val displayStart = mark.startByte + adjustmentOffset
                val displayEnd = mark.endByte + adjustmentOffset
                adjustedMarks.add(SpeechMark(displayStart.toFloat() / 1000, displayEnd.toFloat() / 1000, mark.time, mark.type, mark.value))
            }
        }

        return adjustedMarks
    }


    private fun findStartIndex(startByte: Int): Int {
        var index = 0
        var byteCount = 0
        while (index < scriptTextView.text.length) {
            val char = scriptTextView.text[index]
            byteCount += char.toString().toByteArray().size
            if (byteCount >= startByte) {
                return index
            }
            index++
        }
        return index
    }

    private fun findEndIndex(endByte: Int): Int {
        var index = 0
        var byteCount = 0
        while (index < scriptTextView.text.length) {
            val char = scriptTextView.text[index]
            byteCount += char.toString().toByteArray().size
            if (byteCount >= endByte) {
                return index + 1 // 마지막 단어를 포함시키기 위해 +1
            }
            index++
        }
        return index
    }
    //원래 단어로 변환된 스피치 마크와, 파싱한 스크립트를 비교하여 새로운 스피치마크 생성
    private fun generateNewSpeechMark(parsingScript: List<String>, speechMarks: List<SpeechMark>): List<SpeechMark> {
        val speechMark = mutableListOf<SpeechMark>()
        var currentSpeechMarkIndex = 0
        var scriptIndex = 0
        for (script in parsingScript) {

            // start와 end 인덱스 구하기
            val start = scriptIndex
            val end = start + script.length
            scriptIndex = end

            // script와 speechmark의 value 값이 일치하면 해당 값 적용, 인덱스 증가
            if (currentSpeechMarkIndex < speechMarks.size) { //여기를 contains로 바꿔서 예외처리?
                val mark = speechMarks[currentSpeechMarkIndex]
                if (script == mark.value) {
                    speechMark.add(SpeechMark(start, end, mark.time, mark.type, script))
                    currentSpeechMarkIndex++
                    if (currentSpeechMarkIndex >= speechMarks.size) {
                        currentSpeechMarkIndex--
                    }
                } else if (currentSpeechMarkIndex == 0) {
                    //이전 스피치 마크가 없으면,
                    speechMark.add(SpeechMark(start, end, 0, mark.type, script))
                } else {
                    // 일치하지 않으면 이전 값을 그대로 적용
                    val previousMark = speechMarks[currentSpeechMarkIndex - 1]
                    speechMark.add(SpeechMark(start, end, previousMark.time, previousMark.type, script))
                }
            } else {
                // speechMarks 리스트를 벗어나면 이전 값 그대로 적용
                val previousMark = speechMarks[currentSpeechMarkIndex - 1]
                speechMark.add(SpeechMark(start, end, previousMark.time, previousMark.type, script))
            }
        }
        return speechMark
    }
    */
}
