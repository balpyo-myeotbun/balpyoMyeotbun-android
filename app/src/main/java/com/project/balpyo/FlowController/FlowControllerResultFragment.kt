package com.project.balpyo.FlowController

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerResultBinding
import com.project.balpyo.scriptplay.ScriptSynchronizer

class FlowControllerResultFragment() : Fragment() {
    private lateinit var viewDataBinding: FragmentFlowControllerResultBinding
    private var script = "안녕하세요, 발표 몇분입니다.빠르기를 조정해드릴게요."
    var mediaplayer : MediaPlayer?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_flow_controller_result,
            null,
            false
        )
        initToolBar()
        val spannable = SpannableStringBuilder(script)

        // 특정 텍스트 패턴을 찾아서 회색으로 변경합니다.
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
        viewDataBinding.FCRScript.text = spannable

        var scriptSynchronizer = context?.resources?.let {
            ScriptSynchronizer(
                script,
                viewDataBinding.FCRScript, activity, it.getColor(R.color.primary),
                viewDataBinding.PCTimeBar, viewDataBinding.PCPlayBtn, viewDataBinding.PCStartTimeTextView, viewDataBinding.PCEndTimeTextView,
                500.toLong(), 150.toLong()
            )
        }
        viewDataBinding.PCPlayBtn.setOnClickListener {
            if (scriptSynchronizer != null) {
                if(scriptSynchronizer.isPlaying) {
                    scriptSynchronizer.pause()
                }
                else{
                    scriptSynchronizer.play()
                }
            }
        }

        return viewDataBinding.root
    }

    fun initToolBar() {
        viewDataBinding.run {
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.buttonBack.visibility = View.INVISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonClose.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
        }
    }
}