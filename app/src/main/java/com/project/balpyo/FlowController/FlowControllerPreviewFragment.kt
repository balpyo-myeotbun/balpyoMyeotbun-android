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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerPreviewBinding
import kotlinx.coroutines.launch


class FlowControllerPreviewFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var binding:FragmentFlowControllerPreviewBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        flowControllerViewModel =
            ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding = FragmentFlowControllerPreviewBinding.inflate(layoutInflater)
        initToolBar()
        binding.tvScript.movementMethod = ScrollingMovementMethod.getInstance()
        var script = flowControllerViewModel.getCustomScriptData().value
        if (script != null) {
            script = script.replace("숨 고르기+1", getString(R.string.breathButton))
            script = script.replace("PPT 넘김+2", getString(R.string.pptButton))

            val spannable = SpannableStringBuilder(script)
            val scriptCharSequence: CharSequence = script
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

            binding.tvScript.text = spannable
        }

        binding.btnGenerate.setOnClickListener {
            generateFlowController()
        }
        binding.btnEdit.setOnClickListener {
            findNavController().popBackStack(R.id.flowControllerEditScriptFragment, false)
        }
        return binding.root
    }
    private fun generateFlowController() {
        viewLifecycleOwner.lifecycleScope.launch {
            flowControllerViewModel.generateFlowControllerForStorage(mainActivity, findNavController())
        }
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "발표 연습"
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "4/5"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

}