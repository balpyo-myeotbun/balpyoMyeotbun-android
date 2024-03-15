package com.project.balpyo.TimeCalculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentTimeCalculatorTimeBinding

class TimeCalculatorTimeFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorTimeBinding

    var noSuchTime = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimeCalculatorTimeBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {
            spinnerMinute.run {
                wrapSelectorWheel = false
                minValue = 0
                maxValue = 59
            }
            spinnerMinute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            spinnerSecond.run {
                wrapSelectorWheel = false
                minValue = 0
                maxValue = 59
            }
            spinnerSecond.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            imageViewCheckbox.setOnClickListener {
                if(noSuchTime) {
                    imageViewCheckbox.setImageResource(R.drawable.ic_checkbox_unselected)
                    noSuchTime = false
                } else {
                    imageViewCheckbox.setImageResource(R.drawable.ic_checkbox_selected)
                    noSuchTime = true
                }
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
                text = "2/3"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
        }
    }

}