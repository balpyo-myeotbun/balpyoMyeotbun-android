package com.project.balpyo.TimeCalculator

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.balpyo.databinding.FragmentTimeCalculatorSpeedBinding
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams


class TimeCalculatorSpeedFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorSpeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimeCalculatorSpeedBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {
            seekbar.setOnSeekChangeListener(object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    Log.i(TAG, seekParams.seekBar.toString())
                    Log.i(TAG, seekParams.progress.toString())
                    Log.i(TAG, seekParams.progressFloat.toString())
                    Log.i(TAG, seekParams.fromUser.toString())
                    //when tick count > 0
                    Log.i(TAG, seekParams.thumbPosition.toString())
                    Log.i(TAG, seekParams.tickText)
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
            })
        }
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