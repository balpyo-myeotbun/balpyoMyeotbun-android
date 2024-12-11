package com.project.balpyo.FlowController

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerEditScriptBinding
import kr.bydelta.koala.okt.SentenceSplitter
import kotlin.properties.Delegates

class FlowControllerEditScriptFragment : Fragment() {
    lateinit var binding: FragmentFlowControllerEditScriptBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StorageViewModel
    var editTextMarginBottom by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding = FragmentFlowControllerEditScriptBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]

        binding.run{
            editTextMarginBottom = etScript.marginBottom

            etScript.text = Editable.Factory.getInstance().newEditable(flowControllerViewModel.getNormalScriptData().value.toString())
            btnBottomNext.isEnabled = etScript.text.isNotEmpty()
            btnKeyboardNext.isEnabled = etScript.text.isNotEmpty()

            etScript.addTextChangedListener {
                btnBottomNext.isEnabled = etScript.text.isNotEmpty()
                btnKeyboardNext.isEnabled = etScript.text.isNotEmpty()
            }

            btnBottomNext.setOnClickListener{
                setNextButtonOnClickListener()
            }

            btnKeyboardNext.setOnClickListener {
                setNextButtonOnClickListener()
            }
            btnLoadScript.setOnClickListener {
                viewModel.getStorageListForBottomSheet(this@FlowControllerEditScriptFragment.parentFragmentManager, mainActivity)
            }
        }
        viewModel.storageDetailForBottomSheet.observe(mainActivity){
            if (it != null) {
                flowControllerViewModel.setNormalScript(it.content.toString())
                binding.etScript.setText(flowControllerViewModel.getNormalScriptData().value)
            }
        }
        observeKeyboardState()
        initToolBar()

        return binding.root
    }
    private fun setNextButtonOnClickListener() {
        flowControllerViewModel.setNormalScript(binding.etScript.text.toString())

        //대본을 문장으로 나눔
        val splitter = SentenceSplitter()
        val paragraph = splitter.sentences(flowControllerViewModel.getNormalScriptData().value.toString())
        flowControllerViewModel.setSplitScriptToSentences(paragraph)
        findNavController().navigate(R.id.flowControllerAddTimeFragment)
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
                //스크립트 마진 : 다음 버튼 크기 + 기본 마진
                binding.etScript.updateMargins(bottom = editTextMarginBottom + binding.btnBottomNext.height)
                binding.btnKeyboardNext.visibility = View.VISIBLE
                binding.btnBottomNext.visibility = View.GONE
                binding.btnKeyboardNext.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.etScript.updateMargins(bottom = editTextMarginBottom)
                binding.btnKeyboardNext.visibility = View.GONE
                binding.btnBottomNext.visibility = View.VISIBLE
            }
        }
    }

    //동적으로 마진 설정하는 확장 함수
    fun View.updateMargins(
        left: Int = marginLeft,
        top: Int = marginTop,
        right: Int = marginRight,
        bottom: Int = marginBottom
    ) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(left, top, right, bottom)
        layoutParams = params
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
