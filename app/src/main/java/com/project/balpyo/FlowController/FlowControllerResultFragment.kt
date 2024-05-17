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
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerResultBinding
import com.project.balpyo.FlowController.ScriptSync.ScriptSynchronizer
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel

class FlowControllerResultFragment() : Fragment() {
    private lateinit var viewDataBinding: FragmentFlowControllerResultBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().onBackPressedDispatcher.addCallback(this.requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })

        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        viewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_flow_controller_result,
            null,
            false
        )
        val script = flowControllerViewModel.getCustomScriptData().value.toString()
        initToolBar()
        viewDataBinding.toolbar.textViewTitle.text = flowControllerViewModel.getTitleData().value
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
                flowControllerViewModel.getCustomScriptData().value.toString(),
                viewDataBinding.FCRScript,
                activity, it.getColor(R.color.primary),
                viewDataBinding.PCTimeBar,
                viewDataBinding.PCPlayBtn,
                viewDataBinding.PCStartTimeTextView,
                viewDataBinding.PCEndTimeTextView,
                flowControllerViewModel.getAudioUrlData().value!!,
                flowControllerViewModel.getSpeedData().value!!
            )
        }

        viewDataBinding.PCEditBtn.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            val flowControllerEditScriptFragment = FlowControllerEditScriptFragment()
            transaction.replace(com.project.balpyo.R.id.fragmentContainerView, flowControllerEditScriptFragment)
            transaction.commit()
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
                val navController = findNavController()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestination, true) // 스택의 처음부터 현재 위치까지 모두 팝
                    .build()

                navController.navigate(R.id.homeFragment, null, navOptions)
                flowControllerViewModel.initialize()
            }
        }
    }
}