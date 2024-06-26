package com.project.balpyo.FlowController

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerEditScriptBinding
import kr.bydelta.koala.okt.SentenceSplitter

class FlowControllerEditScriptFragment() : Fragment() {
    lateinit var binding: FragmentFlowControllerEditScriptBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StorageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding = FragmentFlowControllerEditScriptBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        //requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        initToolBar()

        binding.FCESScript.text = Editable.Factory.getInstance().newEditable(flowControllerViewModel.getNormalScriptData().value.toString())

        binding.FCEDStoreBtn.setOnClickListener {
            viewModel.getStorageListForBottomSheet(this@FlowControllerEditScriptFragment.parentFragmentManager, mainActivity)
        }
        viewModel.storageDetailForBottomSheet.observe(mainActivity){
            if (it != null) {
                flowControllerViewModel.setNormalScript(it.script)
                binding.FCESScript.setText(flowControllerViewModel.getNormalScriptData().value)
                //binding.FCESScript.setText(it.script)
            }
        }
        binding.FCESScript.addTextChangedListener {
            if(binding.FCESScript.text.isNotEmpty()){
                binding.FCEDNextBtn.isEnabled = true
                binding.FCEDKeyboardNextBtn.isEnabled = true
            }
            else{
                binding.FCEDNextBtn.isEnabled = false
                binding.FCEDKeyboardNextBtn.isEnabled = false
            }
        }
        binding.FCEDNextBtn.setOnClickListener {
            flowControllerViewModel.setNormalScript(binding.FCESScript.text.toString())

            //대본을 문장으로 나눔
            val splitter = SentenceSplitter()
            val paragraph = splitter.sentences(flowControllerViewModel.getNormalScriptData().value.toString())
            flowControllerViewModel.setSplitScriptToSentences(paragraph)
            findNavController().navigate(R.id.flowControllerAddTimeFragment2)
        }
        binding.FCEDKeyboardNextBtn.setOnClickListener {
            flowControllerViewModel.setNormalScript(binding.FCESScript.text.toString())

            //대본을 문장으로 나눔
            val splitter = SentenceSplitter()
            val paragraph = splitter.sentences(flowControllerViewModel.getNormalScriptData().value.toString())
            flowControllerViewModel.setSplitScriptToSentences(paragraph)
            findNavController().navigate(R.id.flowControllerAddTimeFragment2)
        }

        observeKeyboardState()

        return binding.root
    }

    private fun observeKeyboardState() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            var originHeight = -1
            if ( binding.root.height > originHeight) {
                originHeight =  binding.root.height
            }

            val visibleFrameSize = Rect()
            binding.root.getWindowVisibleDisplayFrame(visibleFrameSize)

            val visibleFrameHeight = visibleFrameSize.bottom - visibleFrameSize.top
            val keyboardHeight = originHeight - visibleFrameHeight

            if (keyboardHeight > visibleFrameHeight * 0.15) {
                // 키보드가 올라옴
                binding.FCEDKeyboardNextBtn.visibility = View.VISIBLE
                binding.FCEDNextBtn.visibility = View.GONE
                binding.FCEDKeyboardNextBtn.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.FCEDKeyboardNextBtn.visibility = View.GONE
                binding.FCEDNextBtn.visibility = View.VISIBLE
            }
        }
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "발표 연습"
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "2/5"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

}

