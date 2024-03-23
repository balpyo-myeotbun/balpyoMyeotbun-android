package com.project.balpyo.FlowController

import android.content.ContentValues.TAG
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerSpeedBinding
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams


class FlowControllerSpeedFragment : Fragment() {

    lateinit var binding: FragmentFlowControllerSpeedBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel

    lateinit var mediaPlayerMinusOne: MediaPlayer
    lateinit var mediaPlayerMinusTwo: MediaPlayer
    lateinit var mediaPlayerZero: MediaPlayer
    lateinit var mediaPlayerOne: MediaPlayer
    lateinit var mediaPlayerTwo: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFlowControllerSpeedBinding.inflate(layoutInflater)
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]

        mediaPlayerMinusTwo = MediaPlayer.create(requireContext(), R.raw.minustwo)
        mediaPlayerMinusOne = MediaPlayer.create(requireContext(), R.raw.minusone)
        mediaPlayerZero = MediaPlayer.create(requireContext(), R.raw.zero)
        mediaPlayerOne = MediaPlayer.create(requireContext(), R.raw.one)
        mediaPlayerTwo = MediaPlayer.create(requireContext(), R.raw.two)

        initToolBar()

        binding.run {
            buttonNext.setOnClickListener {
                findNavController().navigate(R.id.flowControllerPreviewFragment)
            }
            seekbar.setOnSeekChangeListener(object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    Log.i(TAG, seekParams.seekBar.toString())
                    Log.i(TAG, seekParams.progress.toString())
                    Log.i(TAG, seekParams.progressFloat.toString())
                    Log.i(TAG, seekParams.fromUser.toString())
                    //when tick count > 0
                    Log.i(TAG, seekParams.thumbPosition.toString())
                    Log.i(TAG, seekParams.tickText)


                    when(seekParams.tickText.toString()) {
                        "-2" -> {
                            mediaPlayerMinusTwo = MediaPlayer.create(requireContext(), R.raw.minustwo)
                            mediaPlayerMinusOne.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerMinusTwo.start()
                            Log.d("발표몇분","-2")
                            flowControllerViewModel.setSpeed("-2")
                        }
                        "-1" -> {
                            mediaPlayerMinusOne = MediaPlayer.create(requireContext(), R.raw.minusone)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerMinusOne.start()
                            flowControllerViewModel.setSpeed("-1")
                        }
                        "0" -> {
                            mediaPlayerZero = MediaPlayer.create(requireContext(), R.raw.zero)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerMinusOne.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerZero.start()
                            flowControllerViewModel.setSpeed("0")
                        }
                        "1" -> {
                            mediaPlayerOne = MediaPlayer.create(requireContext(), R.raw.one)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerMinusOne.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerOne.start()
                            flowControllerViewModel.setSpeed("1")
                        }
                        "2" -> {
                            mediaPlayerTwo = MediaPlayer.create(requireContext(), R.raw.two)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerMinusOne.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.start()
                            flowControllerViewModel.setSpeed("2")
                        }
                    }
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
                text = "4/5"
            }
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}