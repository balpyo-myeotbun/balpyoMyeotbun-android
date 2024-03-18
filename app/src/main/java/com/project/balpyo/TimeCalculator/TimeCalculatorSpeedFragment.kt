package com.project.balpyo.TimeCalculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentTimeCalculatorSpeedBinding

class TimeCalculatorSpeedFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorSpeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimeCalculatorSpeedBinding.inflate(layoutInflater)

        initToolBar()
        
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "3/3"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
        }
    }
}