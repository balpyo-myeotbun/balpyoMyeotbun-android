package com.project.balpyo.TimeCalculator

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentTimeCalculatorTitleBinding

class TimeCalculatorTitleFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimeCalculatorTitleBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {
            btnBottomNext.setOnClickListener {
                MyApplication.timeCalculatorTitle = editTextTitle.text.toString()
                findNavController().navigate(R.id.timeCalculatorScriptFragment)
            }
            btnKeyboardNext.setOnClickListener {
                MyApplication.timeCalculatorTitle = editTextTitle.text.toString()
                findNavController().navigate(R.id.timeCalculatorScriptFragment)
            }
            imageViewDeleteBtn.setOnClickListener {
                binding.editTextTitle.text.clear()
                binding.btnBottomNext.isEnabled = false
                binding.btnKeyboardNext.isEnabled = false
            }
            editTextTitle.addTextChangedListener {
                if(binding.editTextTitle.text.isNotEmpty()){
                    binding.btnBottomNext.isEnabled = true
                    binding.btnKeyboardNext.isEnabled = true
                    binding.imageViewDeleteBtn.visibility = View.VISIBLE
                }
                else{
                    binding.btnBottomNext.isEnabled = false
                    binding.btnKeyboardNext.isEnabled = false
                    binding.imageViewDeleteBtn.visibility = View.GONE
                }
            }
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
                binding.btnKeyboardNext.visibility = View.VISIBLE
                binding.btnBottomNext.visibility = View.GONE
                binding.btnKeyboardNext.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.btnKeyboardNext.visibility = View.GONE
                binding.btnBottomNext.visibility = View.VISIBLE
            }
        }
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "시간 계산"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "1/4"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }
}