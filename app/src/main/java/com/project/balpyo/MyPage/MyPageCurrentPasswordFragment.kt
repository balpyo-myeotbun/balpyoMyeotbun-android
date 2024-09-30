package com.project.balpyo.MyPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentMyPageCurrentPasswordBinding

class MyPageCurrentPasswordFragment : Fragment() {

    lateinit var binding: FragmentMyPageCurrentPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyPageCurrentPasswordBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {
            editTextCurrentPassword.addTextChangedListener {
                if(editTextCurrentPassword.text.isNotEmpty()){
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
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "2/2"
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "비밀번호 변경하기"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}