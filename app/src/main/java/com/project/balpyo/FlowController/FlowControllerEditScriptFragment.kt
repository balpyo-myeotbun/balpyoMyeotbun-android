package com.project.balpyo.FlowController

import kr.bydelta.koala.okt.SentenceSplitter
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerEditScriptBinding

class FlowControllerEditScriptFragment() : Fragment() {
    lateinit var binding: FragmentFlowControllerEditScriptBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding = FragmentFlowControllerEditScriptBinding.inflate(layoutInflater)
        initToolBar()
        binding.FCESScript.text = Editable.Factory.getInstance().newEditable(flowControllerViewModel.getNormalScriptData().value.toString())
        binding.FCEDNextBtn.setOnClickListener {
            flowControllerViewModel.setNormalScript(binding.FCESScript.text.toString())

            //대본을 문장으로 나눔
            val splitter = SentenceSplitter()
            val paragraph = splitter.sentences(flowControllerViewModel.getNormalScriptData().value.toString())
            flowControllerViewModel.setSplitScriptToSentences(paragraph)
            findNavController().navigate(R.id.flowControllerAddTimeFragment2)}
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "2/5"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}
