package com.project.balpyo.FlowController

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
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
import kotlin.properties.Delegates

class FlowControllerEditScriptFragment() : Fragment() {
    lateinit var binding: FragmentFlowControllerEditScriptBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StorageViewModel
    var editTextMarginBottom by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding = FragmentFlowControllerEditScriptBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]

        binding.run{
            editTextMarginBottom = etScript.marginBottom

            etScript.text = Editable.Factory.getInstance().newEditable(flowControllerViewModel.getNormalScriptData().value.toString())

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
                flowControllerViewModel.setNormalScript(it.script)
                binding.etScript.setText(flowControllerViewModel.getNormalScriptData().value)
            }
        }

        setInsetsListener()
        initToolBar()

        return binding.root
    }
    private fun setNextButtonOnClickListener() {
        flowControllerViewModel.setNormalScript(binding.etScript.text.toString())

        //대본을 문장으로 나눔
        val splitter = SentenceSplitter()
        val paragraph = splitter.sentences(flowControllerViewModel.getNormalScriptData().value.toString())
        flowControllerViewModel.setSplitScriptToSentences(paragraph)
        findNavController().navigate(R.id.flowControllerAddTimeFragment2)
    }
    private fun setInsetsListener(){
        //adjustResize 버전 별 대응
        //키보드 올라옴에 따라 인셋 변경 (안하면 시스템 UI에 의해 키보드 위 다음 버튼에 여백 생김)
        binding.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                root.setOnApplyWindowInsetsListener { _, windowInsets ->
                    //키보드
                    val imeInsets = windowInsets.getInsets(WindowInsets.Type.ime())
                    //네비게이션 바
                    val navBarInsets = windowInsets.getInsets(WindowInsets.Type.navigationBars())

                    val keyboardHeight = imeInsets.bottom
                    val navBarHeight = navBarInsets.bottom

                    if (keyboardHeight > navBarHeight) {
                        //키보드 올라옴
                        //스크립트 마진 : 다음 버튼 크기 + 기본 마진
                        etScript.updateMargins(bottom = editTextMarginBottom + binding.btnBottomNext.height)
                        // 키보드의
                        root.setPadding(0, 0, 0, keyboardHeight - navBarHeight)
                        btnKeyboardNext.visibility = View.VISIBLE
                        btnLoadScript.visibility = View.GONE
                        btnBottomNext.visibility = View.GONE
                    } else {
                        //키보드 내려감
                        etScript.updateMargins(bottom = editTextMarginBottom)
                        root.setPadding(0, 0, 0, 0)
                        btnKeyboardNext.visibility = View.GONE
                        btnLoadScript.visibility = View.VISIBLE
                        btnBottomNext.visibility = View.VISIBLE
                    }
                    windowInsets
                }
            } else {
                requireActivity().window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                )
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
