package com.project.balpyo.MyPage

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.databinding.FragmentMyPageAccountEditBinding

class MyPageAccountEditFragment : Fragment() {
    lateinit var binding: FragmentMyPageAccountEditBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageAccountEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()
        observeKeyboardState()

        binding.run {

            buttonNextKeyboard.setOnClickListener {

            }

            buttonNext.setOnClickListener {

            }

            editTextEmail.setText(PreferenceHelper.getUserId(mainActivity))
            editTextNickname.setText(PreferenceHelper.getUserNickname(mainActivity))

            editTextEmail.addTextChangedListener {
                if(editTextEmail.text.isNotEmpty() && PreferenceHelper.getUserId(mainActivity) != editTextEmail.text.toString() && editTextNickname.text.isNotEmpty()) {
                    buttonNext.isEnabled = true
                    buttonNextKeyboard.isEnabled = true
                }
                else{
                    buttonNext.isEnabled = false
                    buttonNextKeyboard.isEnabled = false
                }
            }

            editTextNickname.addTextChangedListener {
                if(editTextNickname.text.isNotEmpty() && PreferenceHelper.getUserId(mainActivity) != editTextNickname.text.toString() && editTextEmail.text.isNotEmpty()) {
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
            toolbar.imageViewButtonBack.visibility = View.VISIBLE
            toolbar.imageViewButtonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "내 정보 수정"
            toolbar.imageViewButtonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
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