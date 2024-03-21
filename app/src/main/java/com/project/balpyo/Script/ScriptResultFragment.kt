package com.project.balpyo.Script

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptResultBinding

class ScriptResultFragment : Fragment() {

    lateinit var binding: FragmentScriptResultBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: GenerateScriptViewModel

    var editable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptResultBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[GenerateScriptViewModel::class.java]
        viewModel.run {
            script.observe(mainActivity) {
                binding.editTextScript.setText(it)
                Log.d("발표몇분", "${it}")
            }
        }

        initToolBar()
        binding.textViewEdit.visibility = View.INVISIBLE
        binding.editTextScript.isFocusableInTouchMode = false

        binding.run {
            var minute = (MyApplication.scrpitTime.toInt()) / 60
            var second = (MyApplication.scrpitTime.toInt()) % 60

            textViewGoalTime.text = "${minute}분 ${second}초에 맞는"
            textViewSuccess.text = "${MyApplication.scriptTitle} 대본이\n완성되었어요!"

            var fullText = textViewSuccess.text

            val spannableString = SpannableString(fullText)

            // 시작 인덱스와 끝 인덱스 사이의 텍스트에 다른 색상 적용
            val startIndex = 0
            val endIndex = MyApplication.scriptTitle.length
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#EB2A63")), // 색상 설정
                startIndex, // 시작 인덱스
                endIndex, // 끝 인덱스 (exclusive)
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE // 스타일 적용 범위 설정
            )

            textViewSuccess.text = spannableString

            buttonEdit.setOnClickListener {
                if(editable) {
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
                    buttonEdit.text = "대본 수정하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray3))
                    textViewEdit.visibility = View.INVISIBLE
                    editTextScript.isFocusableInTouchMode = false
                } else {
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray2)
                    buttonEdit.text = "대본 수정 완료"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                    textViewEdit.visibility = View.VISIBLE
                    editTextScript.isFocusableInTouchMode = true
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