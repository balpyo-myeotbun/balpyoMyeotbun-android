package com.project.balpyo.TimeCalculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            buttonNext.setOnClickListener {
                MyApplication.timeCalculatorTitle = editTextTitle.text.toString()
                findNavController().navigate(R.id.timeCalculatorScriptFragment)
            }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
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