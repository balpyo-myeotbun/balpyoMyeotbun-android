package com.project.balpyo.Script

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
import com.project.balpyo.databinding.FragmentScriptTopicBinding

class ScriptTopicFragment : Fragment() {

    lateinit var binding: FragmentScriptTopicBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptTopicBinding.inflate(layoutInflater)

        initToolBar()
        observeKeyboardState()

        binding.run {
            buttonNext.setOnClickListener {
                moveFragment()
            }

            buttonNextKeyboard.setOnClickListener {
                moveFragment()
            }

            editTextTopic.addTextChangedListener {
                if(editTextTopic.text.isNotEmpty()){
                    buttonNext.isEnabled = true
                    buttonNextKeyboard.isEnabled = true
                }
                else{
                    buttonNext.isEnabled = false
                    buttonNextKeyboard.isEnabled = false
                }
            }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.text = "대본 생성"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "2/5"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }

    fun moveFragment() {
        MyApplication.scriptTopic = binding.editTextTopic.text.toString()
        findNavController().navigate(R.id.scriptSubtopicFragment)
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
                binding.buttonNextKeyboard.visibility = View.VISIBLE
                binding.buttonNext.visibility = View.GONE
                binding.buttonNextKeyboard.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.buttonNextKeyboard.visibility = View.GONE
                binding.buttonNext.visibility = View.VISIBLE
            }
        }
    }
}