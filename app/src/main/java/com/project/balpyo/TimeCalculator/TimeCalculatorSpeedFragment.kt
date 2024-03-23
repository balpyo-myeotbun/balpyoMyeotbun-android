package com.project.balpyo.TimeCalculator

import android.content.ContentValues.TAG
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.project.balpyo.LoadingFragment
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentTimeCalculatorSpeedBinding
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams


class TimeCalculatorSpeedFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorSpeedBinding

    lateinit var mediaPlayerMinusOne: MediaPlayer
    lateinit var mediaPlayerMinusTwo: MediaPlayer
    lateinit var mediaPlayerZero: MediaPlayer
    lateinit var mediaPlayerOne: MediaPlayer
    lateinit var mediaPlayerTwo: MediaPlayer

    var selectedSpeed = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimeCalculatorSpeedBinding.inflate(layoutInflater)

        mediaPlayerMinusTwo = MediaPlayer.create(requireContext(), R.raw.minustwo)
        mediaPlayerMinusOne = MediaPlayer.create(requireContext(), R.raw.minusone)
        mediaPlayerZero = MediaPlayer.create(requireContext(), R.raw.zero)
        mediaPlayerOne = MediaPlayer.create(requireContext(), R.raw.one)
        mediaPlayerTwo = MediaPlayer.create(requireContext(), R.raw.two)

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

                    selectedSpeed = seekParams.tickText.toString()


                    when(seekParams.tickText.toString()) {
                        "-2" -> {
                            mediaPlayerMinusTwo = MediaPlayer.create(requireContext(), R.raw.minustwo)
                            mediaPlayerMinusOne.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerMinusTwo.start()
                            Log.d("발표몇분","-2")
                        }
                        "-1" -> {
                            mediaPlayerMinusOne = MediaPlayer.create(requireContext(), R.raw.minusone)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerMinusOne.start()
                        }
                        "0" -> {
                            mediaPlayerZero = MediaPlayer.create(requireContext(), R.raw.zero)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerMinusOne.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerZero.start()
                        }
                        "1" -> {
                            mediaPlayerOne = MediaPlayer.create(requireContext(), R.raw.one)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerMinusOne.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerOne.start()
                        }
                        "2" -> {
                            mediaPlayerTwo = MediaPlayer.create(requireContext(), R.raw.two)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerMinusOne.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.start()
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
            })

            buttonNext.setOnClickListener {
                findNavController().navigate(R.id.loadingFragment)
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
                text = "4/4"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }
}