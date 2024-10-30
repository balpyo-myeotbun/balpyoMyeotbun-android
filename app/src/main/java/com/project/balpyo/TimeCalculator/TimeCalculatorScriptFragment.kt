package com.project.balpyo.TimeCalculator

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentTimeCalculatorScriptBinding
import kotlin.properties.Delegates

class TimeCalculatorScriptFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorScriptBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StorageViewModel

    var editTextMarginBottom by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimeCalculatorScriptBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        viewModel.run {
            storageDetailForBottomSheet.observe(mainActivity) {
                if (it != null) {
                    binding.editTextScript.setText(it.content)
                }
            }
        }

        binding.run {
            editTextMarginBottom = editTextScript.marginBottom

            btnBottomNext.setOnClickListener {

                MyApplication.timeCalculatorScript = editTextScript.text.toString()

                findNavController().navigate(R.id.timeCalculatorTimeFragment)
            }

            btnLoadScript.setOnClickListener {
                viewModel.getStorageListForBottomSheet(this@TimeCalculatorScriptFragment.parentFragmentManager, mainActivity)
            }
            editTextScript.addTextChangedListener {
                btnBottomNext.isEnabled = editTextScript.text.isNotEmpty()
                btnKeyboardNext.isEnabled = editTextScript.text.isNotEmpty()
            }
        }

        initToolBar()
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
                //스크립트 마진 : 다음 버튼 크기 + 기본 마진
                binding.editTextScript.updateMargins(bottom = editTextMarginBottom + binding.btnBottomNext.height)
                binding.btnKeyboardNext.visibility = View.VISIBLE
                binding.btnBottomNext.visibility = View.GONE
                binding.btnKeyboardNext.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.editTextScript.updateMargins(bottom = editTextMarginBottom)
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
            toolbar.textViewTitle.text = "시간 계산"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "2/4"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }
}