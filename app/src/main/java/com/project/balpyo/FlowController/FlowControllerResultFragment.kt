package com.project.balpyo.FlowController

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.balpyo.FlowController.BottomSheet.FlowControllerEditBottomSheetFragment
import com.project.balpyo.FlowController.BottomSheet.FlowControllerEditBottomSheetListener
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.api.response.SpeechMark
import com.project.balpyo.databinding.FragmentFlowControllerResultBinding

class FlowControllerResultFragment : Fragment(), FlowControllerEditBottomSheetListener {

    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentFlowControllerResultBinding
    private lateinit var scriptTextView: TextView
    private lateinit var playButton: ImageView
    private lateinit var player: ExoPlayer
    private lateinit var handler: Handler
    private var speechMarks: List<SpeechMark> = listOf()
    private var realSpeechMark : List<SpeechMark>  = listOf()
    private var isPlaying = false
    private lateinit var viewDataBinding: FragmentFlowControllerResultBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel
    private lateinit var storageViewModel: StorageViewModel
    var bottomSheet = FlowControllerEditBottomSheetFragment()
    private val args: FlowControllerResultFragmentArgs by navArgs()
    private lateinit var callback: OnBackPressedCallback
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        binding = FragmentFlowControllerResultBinding.inflate(inflater, container, false)
        scriptTextView = binding.tvScript
        playButton = binding.btnPlay
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        storageViewModel = ViewModelProvider(requireActivity())[StorageViewModel::class.java]
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
        val spannable = SpannableStringBuilder(script)

        scriptTextView.text = spannable
        speechMarks = flowControllerViewModel.getSpeechMarks().value!!

        val breakTimeToRealWord = breakTimeToRealWord(speechMarks)
        Log.d("breakTimeToRealWord", breakTimeToRealWord.toString())
        val endByteToRealEndByte = endByteToRealEndByte(breakTimeToRealWord)
        Log.d("endByteToRealEndByte", endByteToRealEndByte.toString())
        val generateRealSpeechMark = generateRealSpeechMark(script, endByteToRealEndByte)
        Log.d("generateRealSpeechMark", generateRealSpeechMark.toString())
        realSpeechMark = generateRealSpeechMark

        initializeExoPlayer()
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
            //TODO: 스크립트 수정
        }
        binding.btnSpeed05.setOnClickListener {
            flowControllerViewModel.setSpeed(-1)
            //TODO: 스크립트 수정
        }
        binding.btnSpeed1.setOnClickListener {
            flowControllerViewModel.setSpeed(0)
            //TODO: 스크립트 수정
        }
        binding.btnSpeed15.setOnClickListener {
            flowControllerViewModel.setSpeed(1)
            //TODO: 스크립트 수정
        }
        binding.btnSpeed2.setOnClickListener {
            flowControllerViewModel.setSpeed(2)
            //TODO: 스크립트 수정
        }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.storageEditFlowControllerScriptFragment)
        }
        binding.btnStorage.setOnClickListener {
            mainActivity.binding.bottomNavigation.selectedItemId = R.id.storageFragment
        }
        binding.ivFlowResultMenu.setOnClickListener {
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        return binding.root
    }

    //플레이어 초기화
    private fun initializeExoPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        val mediaItem = MediaItem.fromUri(flowControllerViewModel.getAudioUrlData().value!!)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        onPlayerReady()
                    }
                    Player.STATE_ENDED -> {
                        stopPlayback()
                    }
                }
            }
        })
        handler = Handler(Looper.getMainLooper())
    }
    private fun initializeSeekBar() {
        binding.sbTime.max = player.duration.toInt() // 최대값 설정
        binding.sbTime.progress = 0 // 초기값 설정
        updatePlaybackTime() // 초기 시간 업데이트

        binding.sbTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress.toLong()) // 사용자 조작 시 seek
                    highlightText(progress) // 텍스트 하이라이트
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                pausePlayback() // 재생 일시 정지
                handler.removeCallbacks(updateSeekBarTask) // 업데이트 멈춤
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                startPlayback() // 재생 재개
                handler.post(updateSeekBarTask) // 업데이트 다시 시작
            }
        })

        // SeekBar 주기적 업데이트 시작
        handler.post(updateSeekBarTask)
    }

    private fun onPlayerReady() {
        initializeSeekBar() // Player가 준비되면 SeekBar 초기화
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

    private fun highlightText(progress: Int) {
        val spannableString = SpannableString(scriptTextView.text)
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
                spannableString.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    endIndex,
                    scriptTextView.text.length,
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE
                )
                highlightedLine = scriptTextView.layout.getLineForOffset(endIndex)
                if (highlightedLine < 4) {
                    scrollTextViewToLine(0)
                }
                if (highlightedLine > 4) {
                    scrollTextViewToLine(highlightedLine)
                }
            }
        }
        scriptTextView.text = spannableString
    }

    private fun scrollTextViewToLine(line: Int) {
        val layout = scriptTextView.layout
        val yScroll = layout.getLineTop(line) - layout.getLineTop(4)
        binding.scrollView2.smoothScrollTo(0, yScroll)

    }

    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            if (isPlaying) {
                binding.sbTime.progress = player.currentPosition.toInt()
                updatePlaybackTime()
                highlightText(player.currentPosition.toInt())
                handler.postDelayed(this, 100) // 100ms마다 업데이트
            }
        }
    }

    // 시작 재생
    private fun startPlayback() {
        player.playWhenReady = true
        if (player.playbackState == Player.STATE_ENDED
            || player.currentPosition >= player.duration
            ) {
            player.seekTo(0)
        }
        isPlaying = true
        playButton.setImageDrawable(requireActivity().getDrawable(R.drawable.pause))
        handler.post(updateSeekBarTask)
    }

    // 일시 정지
    private fun pausePlayback() {
        player.playWhenReady = false
        isPlaying = false
        playButton.setImageDrawable(requireActivity().getDrawable(R.drawable.exclude))
    }

    // 정지
    private fun stopPlayback() {
        player.pause()
        player.seekTo(0)
        isPlaying = false
        playButton.setImageDrawable(requireActivity().getDrawable(R.drawable.exclude))
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
    }
    private fun releasePlayer() {
        if (this::player.isInitialized) {
            handler.removeCallbacksAndMessages(null)
            player.release()
        }
    }

    private fun updatePlaybackTime() {
        val currentPosition = player.currentPosition
        val totalTime = player.duration

        val startTime = convertMsToMinutesSeconds(currentPosition)
        val endTimeFormatted = convertMsToMinutesSeconds(totalTime - currentPosition)

        binding.tvStartTime.text = startTime

        binding.tvEndTime.text = if (currentPosition >= totalTime) {
            "00:00" // 재생이 끝난 경우
        } else {
            "-$endTimeFormatted"
        }
    }

    // 재생 시간을 mm:ss 형식으로 변환
    private fun convertMsToMinutesSeconds(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun initToolBar() {
        binding.run {
            if(args.type == "New") {
                toolbar.buttonClose.visibility = View.VISIBLE
                toolbar.buttonBack.visibility = View.INVISIBLE
                ivFlowResultMenu.visibility = View.INVISIBLE
            }
            else {
                toolbar.buttonClose.visibility = View.INVISIBLE
                toolbar.buttonBack.visibility = View.VISIBLE
                ivFlowResultMenu.visibility = View.VISIBLE
            }
            if(args.type == "Home" || args.type == "New") {
                toolbar.buttonBack.setOnClickListener {
                    flowControllerViewModel.initialize()
                    findNavController().popBackStack(R.id.homeFragment, false)
                }
                toolbar.buttonClose.setOnClickListener {
                    flowControllerViewModel.initialize()
                    findNavController().popBackStack(R.id.homeFragment, false)
                }
            }
            else
                toolbar.buttonBack.setOnClickListener {
                    flowControllerViewModel.initialize()
                    findNavController().popBackStack(R.id.storageFragment, false)
                }

            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = flowControllerViewModel.getTitleData().value
            toolbar.textViewPage.visibility = View.INVISIBLE
        }
    }
    override fun onItemSelected(position: Int) {
        if (position == 1) {
            findNavController().navigate(R.id.storageEditFlowControllerScriptFragment)
        }
        else {
            storageViewModel.deleteScript(mainActivity, flowControllerViewModel.getScriptIdData().value!!)
            findNavController().popBackStack()
        }
    }
    //기기의 뒤로가기 버튼을 누를 시
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(args.type == "Home" || args.type == "New") {
                    findNavController().popBackStack(R.id.homeFragment, false)
                }
                else {
                    findNavController().popBackStack(R.id.storageFragment, false)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}
