package com.project.balpyo.Sign

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.balpyo.LoadingFragmentArgs
import com.project.balpyo.LoginFragmentDirections
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentSignUpPasswordBinding
import java.util.regex.Pattern

class SignUpPasswordFragment : Fragment() {
    lateinit var binding: FragmentSignUpPasswordBinding
    var isCheck = false
    var isComplete = false
    var pw : String = ""
    var checkPw : String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpPasswordBinding.inflate(layoutInflater)
        initToolBar()
        observeKeyboardState()

        binding.run {
            etSignupPw.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    pw = s.toString()
                    etSignupCheckPw.setText("")
                    llSignupCheckPw.visibility = View.INVISIBLE

                    if (isPasswordLengthValid(pw)) {
                        ivSignupPw10.setImageResource(R.drawable.ic_check)
                        tvSignupPw10.setTextColor(resources.getColor(R.color.black))
                    } else {
                        ivSignupPw10.setImageResource(R.drawable.ic_uncheck)
                        tvSignupPw10.setTextColor(resources.getColor(R.color.disabled))
                    }
                    if (isPasswordComplex(pw)) {
                        ivSignupCombination.setImageResource(R.drawable.ic_check)
                        tvSignupCombination.setTextColor(resources.getColor(R.color.black))
                    } else {
                        ivSignupCombination.setImageResource(R.drawable.ic_uncheck)
                        tvSignupCombination.setTextColor(resources.getColor(R.color.disabled))
                    }

                    isCheck = isPasswordLengthValid(pw) && isPasswordComplex(pw)

                    btnSignupPwNext.isEnabled = isCheck
                    btnKeyboardNext.isEnabled = isCheck
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            etSignupCheckPw.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    checkPw = s.toString()

                    isComplete = pw == checkPw
                    btnSignupPwNext.isEnabled = isCheck && isComplete
                    btnKeyboardNext.isEnabled = isCheck && isComplete
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            btnKeyboardNext.setOnClickListener {
                if(isPasswordLengthValid(pw) && isPasswordComplex(pw) && pw == checkPw){
                    MyApplication.password = pw
                    findNavController().navigate(R.id.signUpTermsFragment)
                }
                else{
                    llSignupCheckPw.visibility = View.VISIBLE
                    btnKeyboardNext.isEnabled = false
                    btnSignupPwNext.isEnabled = false
                    llSignupCheckPw.requestFocus()
                    showKeyboard(etSignupCheckPw)
                }
            }
            btnSignupPwNext.setOnClickListener{
                if(isPasswordLengthValid(pw) && isPasswordComplex(pw) && pw == checkPw){
                    MyApplication.password = pw
                    val action = LoginFragmentDirections.actionLoginFragmentToSignUpTermsFragment(
                        type = null
                    )
                    findNavController().navigate(action)
                }
                else{
                    llSignupCheckPw.visibility = View.VISIBLE
                    btnKeyboardNext.isEnabled = false
                    btnSignupPwNext.isEnabled = false
                    llSignupCheckPw.requestFocus()
                    showKeyboard(etSignupCheckPw)
                }
            }
        }

        return binding.root
    }
    private fun isPasswordLengthValid(password: String): Boolean {
        return password.length >= 10
    }
    private fun isPasswordComplex(password: String): Boolean {
        // 패턴 정의
        val pwPattern1 = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{2,}$" // 영문, 숫자
        val pwPattern2 = "^(?=.*[0-9])(?=.*[$@$!%*#?&])[0-9$@$!%*#?&]{2,}$" // 숫자, 특수문자
        val pwPattern3 = "^(?=.*[A-Za-z])(?=.*[$@$!%*#?&])[A-Za-z$@$!%*#?&]{2,}$" // 영문, 특수문자
        val pwPattern4 = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z0-9$@$!%*#?&]{2,}$" // 영문, 숫자, 특수문자

        // 패턴 매칭
        return (Pattern.matches(pwPattern1, password) ||
                Pattern.matches(pwPattern2, password) ||
                Pattern.matches(pwPattern3, password) ||
                Pattern.matches(pwPattern4, password))
    }
    private fun showKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
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
                binding.btnKeyboardNext.visibility = View.VISIBLE
                binding.btnSignupPwNext.visibility = View.GONE
                binding.btnKeyboardNext.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.btnKeyboardNext.visibility = View.GONE
                binding.btnSignupPwNext.visibility = View.VISIBLE
            }
        }
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "2/3"
            toolbar.textViewTitle.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}