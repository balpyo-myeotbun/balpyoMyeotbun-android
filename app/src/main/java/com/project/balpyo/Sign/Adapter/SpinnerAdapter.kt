package com.project.balpyo.Sign.Adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.project.balpyo.R
import com.project.balpyo.Sign.SignUpEmailFragment
import com.project.balpyo.databinding.SpinnerItemBackgroundBinding
import com.project.balpyo.databinding.SpinnerItemBackgroundEditBinding

class SpinnerAdapter<T>(context: Context, val fragment: SignUpEmailFragment) :
    ArrayAdapter<String>(context, R.layout.spinner_default_background, listOf("직접 입력", "gmail.com", "naver.com", "hanmail.com", "kakao.com", "nate.com", "outlook.com", "icloud.com")) {

    lateinit var editbinding: SpinnerItemBackgroundEditBinding
    private var mSelectedIndex = -1

    fun setSelection(position: Int) {
        mSelectedIndex = position
        notifyDataSetChanged()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = SpinnerItemBackgroundBinding.inflate(LayoutInflater.from(context), parent, false)
        binding.tvSpinnerItemBackground.text = this.getItem(position)

        // 선택된 아이템의 글자 색상 변경
        if (position == mSelectedIndex) {
            binding.tvSpinnerItemBackground.setTextColor(context.getColor(R.color.primary))
        } else {
            binding.tvSpinnerItemBackground.setTextColor(context.getColor(R.color.black))
        }

        return binding.root
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        editbinding = SpinnerItemBackgroundEditBinding.inflate(LayoutInflater.from(context), parent, false)
        if (position == 0) {
            editbinding.tvSpinnerItemBackgroundEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        fragment.domain = s.toString()
                        fragment.isRegularEmail()
                    } else {
                        fragment.isRegularEmail()
                    }
                }
            })
            return editbinding.root
        }
        return view
    }


    fun initListener() {
        val binding = SpinnerItemBackgroundEditBinding.inflate(LayoutInflater.from(context))
        binding.ivShowDropdown.setOnClickListener {
            binding.tvSpinnerItemBackgroundEdit.isEnabled = false
        }
    }

    fun submitList(list: List<String>) {
        this.clear()
        this.addAll(list)
        this.notifyDataSetChanged()
    }
}