package com.project.balpyo.FlowController.ScriptSync

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.project.balpyo.R
import java.util.*

class ScriptSynchronizer(
    val script: String,
    val textView: TextView,
    val activity: FragmentActivity?,
    val color: Int,
    val pcTimeBar: SeekBar,
    val pcPlayBtn: ImageView,
    val pcStartTimeTextView: TextView,
    val pcEndTimeTextView: TextView,
    val restDuration : Long,
    val normalCharacterDuration : Long
) {
    var mPlayer: MediaPlayer
    private var currentIndex = 0
    private var timer: Timer? = null
    var isPlaying = false
    private var totalDuration: Long = 0

    init {
        calculateTotalDuration()
        mPlayer = MediaPlayer()
        val uri: Uri = Uri.parse("android.resource://" + activity!!.packageName + "/" + R.raw.speech)
        mPlayer.setDataSource(activity!!.applicationContext, uri)
        mPlayer.prepare()
        activity?.runOnUiThread {
            pcTimeBar.max = totalDuration.toInt()
            pcEndTimeTextView.text = convertMsToMinutesSeconds(totalDuration)
            setupSeekBarListener()
        }
    }

    //밀리 초를 mm:ss로 변환
    fun convertMsToMinutesSeconds(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d", minutes, seconds)
    }

    //스크립트의 총 재생시간 구하기
    private fun calculateTotalDuration() {
        totalDuration = script.fold(0L) { acc, c ->
            acc + when (c) {
                ',' -> restDuration //띄어쓰기와 쉼표의 ms
                '.', '\n' -> 0 //엔터와 온점의 ms
                else -> getSpecialTextDelay() ?: normalCharacterDuration //특정 텍스트의 지연시간 or 일반 문자의 지연시간
            }
        }
        // "숨 고르기 (1초)", "PPT 넘김 (2초)" 의 길이
        val specialTexts = mapOf("숨 고르기 (1초)" to 1000L, "PPT 넘김 (2초)" to 2000L)
        specialTexts.forEach { (text, delay) ->
            Regex(text).findAll(script).forEach { _ ->
                totalDuration += delay
            }
        }
    }

    //seekbar를 조정할때 updateCurrentIndex로 progress에 맞게 current인덱스를 조정, 음성파일 조정
    private fun setupSeekBarListener() {
        pcTimeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateCurrentIndex(progress.toLong())
                    mPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    //progress에 맞게 current인덱스를 조정 후 텍스트 하이라이트 변경
    private fun updateCurrentIndex(progress: Long) {
        var accumulatedTime: Long = 0
        var index = 0

        // 조정한 seekbar의 프로그레스까지의 스크립트 시간을 계산하며 인덱스를 계산한다.
        // 계산된 인덱스를 currentIndex에 반영하여 하이라이팅 되는 스크립트를 변경
        while (index < script.length && accumulatedTime < progress) {
            val currentChar = script[index].toString()
            val delay = when (currentChar) {
                "," -> restDuration
                ".", "\n" -> 0
                else -> getSpecialTextDelayForSubstring(script.substring(index)) ?: normalCharacterDuration
            }
            accumulatedTime += delay

            // 특수 텍스트의 지연 시간을 고려하여 인덱스를 증가
            if (delay > normalCharacterDuration) {
                val specialTextLength = getSpecialTextLengthStartingAt(script.substring(index))
                if (specialTextLength > 0) {
                    index += specialTextLength
                    continue
                }
            }

            index++
        }

        currentIndex = index
        highlightCurrentCharacter()
    }

    //특수 텍스트의 딜레이 값 반환
    private fun getSpecialTextDelayForSubstring(substring: String): Long? {
        val specialTexts = mapOf(
            "숨 고르기 (1초)" to 1000L,
            "PPT 넘김 (2초)" to 2000L
        )

        specialTexts.forEach { (text, delay) ->
            if (substring.startsWith(text)) {
                return delay
            }
        }

        return null
    }

    //특수 텍스트의 길이 계산
    private fun getSpecialTextLengthStartingAt(substring: String): Int {
        val specialTexts = listOf("숨 고르기 (1초)", "PPT 넘김 (2초)")

        specialTexts.forEach { text ->
            if (substring.startsWith(text)) {
                return text.length
            }
        }

        return 0
    }

    private fun scheduleNextCharacter() {
        val delay: Long = when (val currentChar = script.getOrNull(currentIndex)) {
            ',' -> restDuration
            '.', '\n' -> 0
            else -> getSpecialTextDelay() ?: normalCharacterDuration
        }

        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (currentIndex < script.length) {
                    activity?.runOnUiThread {
                        highlightCurrentCharacter()
                    }
                    currentIndex++
                    scheduleNextCharacter() // Schedule the next character
                } else {
                    stop()
                }
            }
        }, delay)
    }

    private fun getSpecialTextDelay(): Long? {
        // "숨 고르기 (1초)" 또는 "PPT 넘김 (2초)"와 같은 특정 문자열에 대한 지연 시간을 반환
        val specialTexts = mapOf(
            "숨 고르기 (1초)" to 1000L,
            "PPT 넘김 (2초)" to 2000L
        )

        specialTexts.forEach { (text, delay) ->
            if (script.substring(currentIndex).startsWith(text)) {
                currentIndex += text.length // 특정 문자열에 대한 길이만큼 인덱스를 증가
                return delay
            }
        }

        return null // 해당 문자열이 없는 경우 null 반환
    }

    private fun highlightCurrentCharacter() {
        // 현재까지의 시간을 계산
        var currentTime: Long = 0
        var index = 0

        while (index < currentIndex) {
            val char = script.getOrNull(index)?.toString()
            currentTime += when (char) {
                "," -> restDuration
                ".", "\n" -> 0
                else -> getSpecialTextDelayForSubstring(script.substring(index)) ?: normalCharacterDuration
            }

            // 특수 텍스트의 지연 시간을 확인하고 추가
            val specialTextLength = getSpecialTextLengthStartingAt(script.substring(index))
            if (specialTextLength > 0) {
                // 특수 텍스트에 대한 지연 시간을 추가
                currentTime += getSpecialTextDelayForSubstring(script.substring(index)) ?: 0
                // 특수 텍스트 길이만큼 인덱스를 증가
                index += specialTextLength
            } else {
                index++
            }
        }

        val spannable = SpannableStringBuilder(script)

        ///숨 고르기랑 ppt 넘김 회색으로 변경
        val patterns = listOf("숨 고르기 \\(1초\\)", "PPT 넘김 \\(2초\\)")
        patterns.forEach { pattern ->
            val regex = Regex(pattern)
            regex.findAll(script).forEach { matchResult ->
                val start = matchResult.range.first
                val end = matchResult.range.last + 1
                spannable.setSpan(
                    ForegroundColorSpan(Color.GRAY),
                    start,
                    end,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        if (currentIndex in script.indices) {
            spannable.setSpan(
                ForegroundColorSpan(color),
                0,
                currentIndex + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val scrollView = textView.parent.parent as? ScrollView ?: return

        // TextView의 Layout에서 현재 라인의 Y 좌표를 가져오기
        val layout = textView.layout ?: return
        val line = layout.getLineForOffset(currentIndex)
        val topOfLine = layout.getLineTop(line)
        val lineHeight = layout.getLineBottom(line) - topOfLine

        // 현재 라인의 중앙 위치를 계산하기
        val lineCenter = topOfLine + lineHeight / 2

        // ScrollView에서 TextView의 상단 높이를 제거
        val scrollY = lineCenter - (scrollView.height / 2)

        // 최종 스크롤 위치를 조정 , 스크롤이 범위를 벗어나지 않도록
        val finalScrollY = clamp(scrollY, 0, textView.height - scrollView.height)

        scrollView.smoothScrollTo(0, finalScrollY)

        activity?.runOnUiThread {
            // TextView 업데이트
            textView.text = spannable
            // 프로그레스 바 업데이트
            pcTimeBar.progress = (currentTime.toFloat() / totalDuration * pcTimeBar.max).toInt()
            // 시작 시간 텍스트 업데이트
            pcStartTimeTextView.text = convertMsToMinutesSeconds(currentTime)
        }
    }
    // 스크롤 위치가 ScrollView의 스크롤 가능 범위를 넘지 않도록 값을 제한
    fun clamp(value: Int, min: Int, max: Int): Int {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }

    //tts와 스크립트 하이라이팅 시작
    fun play() {
        if (!isPlaying) {
            mPlayer.start()
            mPlayer.seekTo(pcTimeBar.progress)
            mPlayer.isLooping = false
            timer = Timer()
            isPlaying = true
            activity?.runOnUiThread {
                pcPlayBtn.setImageDrawable(activity.getDrawable(R.drawable.pause))
            }
            scheduleNextCharacter()
        }
    }
    fun pause() {
        if (isPlaying) {
            mPlayer.pause()
            timer?.cancel()
            isPlaying = false
            activity?.runOnUiThread {
                pcPlayBtn.setImageDrawable(activity.getDrawable(R.drawable.exclude))
            }
        }
    }

    fun stop() {
        mPlayer.stop()      //음악 정지
        mPlayer.seekTo(0)
        timer?.cancel()
        pcTimeBar.progress = 0
        currentIndex = 0
        isPlaying = false
        activity?.runOnUiThread {
            pcPlayBtn.setImageDrawable(activity.getDrawable(R.drawable.exclude))
        }
    }
}
