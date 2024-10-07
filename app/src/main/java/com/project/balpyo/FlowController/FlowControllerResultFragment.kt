package com.project.balpyo.FlowController

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.BottomSheet.FlowControllerEditBottomSheetFragment
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Storage.LoadScriptBottomSheet.LoadScriptBottomSheetFragment
import com.project.balpyo.Storage.NoteBottomSheet.NoteBottomSheetFragment
import com.project.balpyo.Storage.NoteBottomSheet.NoteBottomSheetListener
import com.project.balpyo.TimeCalculator.TimeCalculatorResultFragmentDirections
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.response.GenerateAudioResponse
import com.project.balpyo.api.response.SpeechMark
import com.project.balpyo.databinding.FragmentFlowControllerResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FlowControllerResultFragment : Fragment(), NoteBottomSheetListener{

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
    var bottomSheet = NoteBottomSheetFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        binding = FragmentFlowControllerResultBinding.inflate(inflater, container, false)
        scriptTextView = binding.tvScript
        playButton = binding.btnPlay
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        viewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_flow_controller_result,
            null,
            false
        )
        initToolBar()
        binding.sbTime.isEnabled = false

        val speed = flowControllerViewModel.getSpeedData().value
        when(speed) {
            -2 -> {binding.btnSpeed03.setBackgroundResource(R.drawable.selected_speed)
                binding.tvSpeed03.setTextColor(this.resources.getColor(R.color.primary))
            }
            -1 -> {binding.btnSpeed05.setBackgroundResource(R.drawable.selected_speed)
                binding.tvSpeed05.setTextColor(this.resources.getColor(R.color.primary))
            }
            0 -> {binding.btnSpeed1.setBackgroundResource(R.drawable.selected_speed)
                binding.tvSpeed1.setTextColor(this.resources.getColor(R.color.primary))
            }
            1 -> {binding.btnSpeed15.setBackgroundResource(R.drawable.selected_speed)
                binding.tvSpeed15.setTextColor(this.resources.getColor(R.color.primary))
            }
            2 -> {binding.btnSpeed2.setBackgroundResource(R.drawable.selected_speed)
                binding.tvSpeed2.setTextColor(this.resources.getColor(R.color.primary))
            }
            else -> println("none")
        }

        var script = flowControllerViewModel.getCustomScriptData().value.toString()
        script = script.replace("숨 고르기+1", "숨 고르기 (1초)")
        script = script.replace("PPT 넘김+2", "PPT 넘김 (2초)")
        Log.d("", script)
        val spannable = SpannableStringBuilder(script)

        scriptTextView.text = spannable
        speechMarks = flowControllerViewModel.getSpeechMarks().value!!

        val breakTimeToRealWord = breakTimeToRealWord(speechMarks)
        Log.d("breakTimeToRealWord", breakTimeToRealWord.toString())
        val endByteToRealEndByte = endByteToRealEndByte(breakTimeToRealWord)
        Log.d("endByteToRealEndByte", endByteToRealEndByte.toString())
        Log.d("script", script)
        val generateRealSpeechMark = generateRealSpeechMark(script, endByteToRealEndByte)
        Log.d("generateRealSpeechMark", generateRealSpeechMark.toString())
        realSpeechMark = generateRealSpeechMark

        initializeMediaPlayer()
        initializeSeekBar()
        playButton.setOnClickListener {
            if (isPlaying) {
                pausePlayback()
            } else {
                binding.sbTime.isEnabled = true
                startPlayback()
            }
        }
        binding.btnSpeed03.setOnClickListener {
            flowControllerViewModel.setSpeed(-2)
            generateAudio()
        }
        binding.btnSpeed05.setOnClickListener {
            flowControllerViewModel.setSpeed(-1)
            generateAudio()
        }
        binding.btnSpeed1.setOnClickListener {
            flowControllerViewModel.setSpeed(0)
            generateAudio()
        }
        binding.btnSpeed15.setOnClickListener {
            flowControllerViewModel.setSpeed(1)
            generateAudio()
        }
        binding.btnSpeed2.setOnClickListener {
            flowControllerViewModel.setSpeed(2)
            generateAudio()
        }

        binding.btnEdit.setOnClickListener {
            val bottomSheetFragment = FlowControllerEditBottomSheetFragment()
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }
        binding.btnStorage.setOnClickListener {
            mainActivity.binding.bottomNavigation.selectedItemId = R.id.storageFragment
        }
        binding.ivFlowResultMenu.setOnClickListener {
            bottomSheet.show(childFragmentManager,bottomSheet.tag)
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
        binding.tvStartTime.text = startTime
        if(endTime == "00:00" || remainingTime == totalTime)
            binding.tvEndTime.text = endTime
        else
            binding.tvEndTime.text = "-" + endTime
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
        binding.sbTime.max = mediaPlayer.duration
        updatePlaybackTime()
        //seekbar 리스너
        binding.sbTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
                binding.sbTime.progress = currentPosition
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
        "200ms" to listOf("\n"),
        "601ms" to listOf("."),
        "400ms" to listOf(","),
        "600ms" to listOf("!"),
        "801ms" to listOf("?"),
        "1000ms" to listOf("숨 고르기 (1초)"),
        "2000ms" to listOf("PPT 넘김 (2초)")
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
            Log.d("sm", speechMark.toString())
        }
        return speechMark
    }

    private fun highlightText(progress: Int) {
        val spannableString = SpannableString(scriptTextView.text)
        var lastEndIndex = 0
        var highlightedLine = -1

        for (mark in realSpeechMark) {
            if (mark.time - 200 <= progress && progress <= mark.time + 1000) {
                val startIndex = 0
                val endIndex = mark.end

                spannableString.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.primary)),
                    startIndex,
                    endIndex,
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE
                )
                lastEndIndex = endIndex
                highlightedLine = scriptTextView.layout.getLineForOffset(endIndex)
            }
        }

        spannableString.setSpan(
            ForegroundColorSpan(Color.BLACK),
            lastEndIndex,
            scriptTextView.text.length,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        scriptTextView.text = spannableString
        Log.d("lastEndIndex", lastEndIndex.toString())
        Log.d("", scriptTextView.text.length.toString())
        if (highlightedLine < 4) {
            scrollTextViewToLine(0)
        }
        if (highlightedLine > 4) {
            scrollTextViewToLine(highlightedLine)
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
        val action = FlowControllerResultFragmentDirections.actionFlowControllerResultFragmentToLoadingFragment(
            toolbarTitle = "발표연습",
            comment = "화면을 나가면 저장되지 않아요!"
        )
        findNavController().navigate(action)
        var apiClient = ApiClient(mainActivity)

        val request = GenerateAudioRequest(flowControllerViewModel.getCustomScriptData().value.toString(), flowControllerViewModel.getSpeedData().value!!, "1234")
        apiClient.apiService.generateAudio("Bearer ${PreferenceHelper.getUserToken(mainActivity)}","audio/mp3", request)?.enqueue(object :
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
    override fun onNoteSelected(position: Int) {
        if (position == 2) {
            findNavController().navigate(R.id.storageEditFlowControllerScriptFragment)
        }
        else {
            Log.d("fc", "delete")
        }
    }
}
