package com.project.balpyo.Script

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.content.ContextCompat
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentScriptResultBinding

class ScriptResultFragment : Fragment() {

    lateinit var binding: FragmentScriptResultBinding

    var editable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptResultBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {
            buttonEdit.setOnClickListener {
                if(editable) {
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
                    buttonEdit.text = "대본 수정하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray3))
                } else {
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray2)
                    buttonEdit.text = "대본 수정 완료"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                }
                editable = !editable
            }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }

            toolbar.buttonClose.setOnClickListener {
                // 닫기 버튼 클릭시 동작
            }
        }
    }
}