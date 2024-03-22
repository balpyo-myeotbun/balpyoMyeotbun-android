package com.project.balpyo.FlowController

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.databinding.FragmentFlowControllerPreviewBinding

class FlowControllerPreviewFragment : Fragment() {
    lateinit var binding:FragmentFlowControllerPreviewBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding = FragmentFlowControllerPreviewBinding.inflate(layoutInflater)
        initToolBar()
        binding.FCPScript.movementMethod = ScrollingMovementMethod.getInstance()
        val spannable = SpannableStringBuilder(flowControllerViewModel.getCustomScriptData().value)
        val scriptCharSequence: CharSequence = flowControllerViewModel.getCustomScriptData().value!!
        ///숨 고르기랑 ppt 넘김 회색으로 변경
        val patterns = listOf("숨 고르기 \\(1초\\)", "PPT 넘김 \\(2초\\)")
        patterns.forEach { pattern ->
            val regex = Regex(pattern)
            regex.findAll(scriptCharSequence).forEach { matchResult ->
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

        binding.FCPScript.text = spannable

        binding.FCPNextBtn.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            val FlowControllerResultFragment = FlowControllerResultFragment()
            transaction.replace(com.project.balpyo.R.id.fragmentContainerView, FlowControllerResultFragment)
            transaction.commit()
        }
        binding.FCPEditBtn.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            val flowControllerEditScriptFragment = FlowControllerEditScriptFragment()
            transaction.replace(com.project.balpyo.R.id.fragmentContainerView, flowControllerEditScriptFragment)
            transaction.commit()
        }
        return binding.root
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "4/4"
            toolbar.buttonBack.setOnClickListener {
                // Handle back button click
            }
        }
    }
}