package com.project.balpyo.Sign

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.project.balpyo.R
import com.project.balpyo.Sign.Adapter.SpinnerAdapter
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentSignUpEmailBinding
import com.project.balpyo.databinding.SpinnerItemBackgroundEditBinding

class SignUpEmailFragment : Fragment() {
    lateinit var binding: FragmentSignUpEmailBinding
    lateinit var adapter : SpinnerAdapter<String>
    var domain = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpEmailBinding.inflate(layoutInflater)
        initToolBar()
        val bindingEdit = SpinnerItemBackgroundEditBinding.inflate(inflater, container, false)
        adapter = SpinnerAdapter(requireContext(), this)
        adapter.initListener()
        adapter.setDropDownViewResource(R.layout.spinner_item_background)

        binding.run {
            spnSignupEmail.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        adapter.setSelection(position)
                        if (position == 0) {
                            domain = ""  // 직접 입력시 domain 초기화
                            isRegularEmail()
                        } else {
                            domain = parent?.getItemAtPosition(position).toString()
                            isRegularEmail()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }
            spnSignupEmail.adapter = adapter
            bindingEdit.tvSpinnerItemBackgroundEdit.setOnClickListener {
                bindingEdit.tvSpinnerItemBackgroundEdit.isFocusedByDefault = true
                bindingEdit.flSpinnerItemBackgroundEdit.isFocusableInTouchMode = true
                bindingEdit.tvSpinnerItemBackgroundEdit.isFocusableInTouchMode = true
                bindingEdit.flSpinnerItemBackgroundEdit.requestFocus()
            }
            etSignupEmailId.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        isRegularEmail()
                    } else {
                        isRegularEmail()
                    }
                }
            })
            btnSignupEmail.setOnClickListener {
                val enteredDomain = if (domain.isEmpty()) adapter.editbinding.tvSpinnerItemBackgroundEdit.text.toString() else domain
                MyApplication.email = (etSignupEmailId.text.toString().trim() + "@" + enteredDomain.trim())
                findNavController().navigate(R.id.signUpPasswordFragment)
            }
        }
        return binding.root
    }
    fun isRegularEmail(): Boolean {
        val enteredDomain = if (domain.isEmpty()) adapter.editbinding.tvSpinnerItemBackgroundEdit.text.toString() else domain
        val email = (binding.etSignupEmailId.text.toString().trim() + "@" + enteredDomain.trim())
        val pattern = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (pattern) {
            // 유효성 검사 결과 이메일 형식일 경우
            binding.btnSignupEmail.setEnabled(true)
            return true
        } else {
            // 유효성 검사 결과 이메일 형식이 아니면
            binding.btnSignupEmail.setEnabled(false)
            return false
        }
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

}