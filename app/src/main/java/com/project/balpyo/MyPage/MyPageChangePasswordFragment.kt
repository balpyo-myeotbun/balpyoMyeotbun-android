package com.project.balpyo.MyPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentMyPageChangePasswordBinding

class MyPageChangePasswordFragment : Fragment() {

    lateinit var binding: FragmentMyPageChangePasswordBinding

    var regularExpression1 = false
    var regularExpression2 = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyPageChangePasswordBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {

            editTextPasswordCheck.visibility = View.INVISIBLE
            editTextPassword.addTextChangedListener {
                if(editTextPassword.text.isNotEmpty()){
                    isValidPassword()
                }
                else{
                    buttonNext.isEnabled = false
                    buttonNextKeyboard.isEnabled = false
                }
            }

            editTextPasswordCheck.addTextChangedListener {
                if(editTextPasswordCheck.text.isNotEmpty()) {
                    if(editTextPasswordCheck.text.toString() == editTextPassword.text.toString()) {
                        buttonNext.isEnabled = true
                        buttonNextKeyboard.isEnabled = true
                    } else {
                        buttonNext.isEnabled = false
                        buttonNextKeyboard.isEnabled = false
                    }
                }
                else{
                    buttonNext.isEnabled = false
                    buttonNextKeyboard.isEnabled = false
                }
            }

            buttonNext.setOnClickListener {
                if(editTextPasswordCheck.text.isNotEmpty()) {
                    // 비밀번호 변경 API 요청
                } else {
                    editTextPasswordCheck.visibility = View.VISIBLE
                }
            }

            buttonNextKeyboard.setOnClickListener {
                if(editTextPasswordCheck.text.isNotEmpty()) {
                    // 비밀번호 변경 API 요청
                } else {
                    editTextPasswordCheck.visibility = View.VISIBLE
                }
            }
        }

        return binding.root
    }

    fun checkRegularExpression(password: String): Boolean {
        val hasLetter = password.contains(Regex("[a-zA-Z]"))
        val hasDigit = password.contains(Regex("[0-9]"))
        val hasSpecialChar = password.contains(Regex("[^a-zA-Z0-9 ]"))

        val combinationCount = listOf(hasLetter, hasDigit, hasSpecialChar).count { it }

        return combinationCount >= 2
    }

    fun isValidPassword() {
        binding.run {
            if(editTextPassword.length() >= 10) {
                regularExpression1 = true
                textViewRegularExpression1Check.setTextColor(resources.getColor(R.color.black))
                imageViewRegularExpression1Check.setImageResource(R.drawable.ic_check_selected)
            } else {
                regularExpression1 = false
                textViewRegularExpression1Check.setTextColor(resources.getColor(R.color.disabled))
                imageViewRegularExpression1Check.setImageResource(R.drawable.ic_check_unselected)
            }

            if(checkRegularExpression(editTextPassword.text.toString())) {
                regularExpression1 = true
                textViewRegularExpression2Check.setTextColor(resources.getColor(R.color.black))
                imageViewRegularExpression2Check.setImageResource(R.drawable.ic_check_selected)
            } else {
                regularExpression1 = false
                textViewRegularExpression2Check.setTextColor(resources.getColor(R.color.disabled))
                imageViewRegularExpression2Check.setImageResource(R.drawable.ic_check_unselected)
            }

            if(regularExpression1 && regularExpression2) {
                buttonNext.isEnabled = true
                buttonNextKeyboard.isEnabled = true
            } else {
                buttonNext.isEnabled = false
                buttonNextKeyboard.isEnabled = false
            }
        }
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