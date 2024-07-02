package com.project.balpyo.FlowController

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerSpeedBinding


class FlowControllerSpeedFragment : Fragment() {

    lateinit var binding: FragmentFlowControllerSpeedBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel
    private lateinit var callback: OnBackPressedCallback

    private val mediaPlayers: Array<MediaPlayer?> = arrayOfNulls(5)

    private val CLs: Array<ConstraintLayout?> = arrayOfNulls(5)
    private val TVs: Array<TextView?> = arrayOfNulls(5)

    private lateinit var animationDrawable: AnimationDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFlowControllerSpeedBinding.inflate(layoutInflater)
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]

        animationDrawable = binding.ivVolume.drawable as AnimationDrawable

        initToolBar()
        initailizeMediaPlayers()
        initailizeCLsAndTVs()

        flowControllerViewModel.setSpeed(0)
        startPlayer(0, R.raw.zero)
        setSpeedColor(0)

        setSpeedBtnClickListeners()

        binding.run {
            buttonNext.setOnClickListener {
                stopMediaPlayers()
                findNavController().navigate(R.id.flowControllerPreviewFragment)
            }
        }
        return binding.root
    }

    fun initailizeMediaPlayers(){
        mediaPlayers[0] = MediaPlayer.create(requireContext(), R.raw.minustwo)
        mediaPlayers[1] = MediaPlayer.create(requireContext(), R.raw.minusone)
        mediaPlayers[2] = MediaPlayer.create(requireContext(), R.raw.zero)
        mediaPlayers[3] = MediaPlayer.create(requireContext(), R.raw.one)
        mediaPlayers[4] = MediaPlayer.create(requireContext(), R.raw.two)
    }

    fun initailizeCLsAndTVs(){
        binding.run {
            CLs[0] = btnSpeed03
            CLs[1] = btnSpeed05
            CLs[2] = btnSpeed1
            CLs[3] = btnSpeed15
            CLs[4] = btnSpeed2

            TVs[0] = tvSpeed03
            TVs[1] = tvSpeed05
            TVs[2] = tvSpeed1
            TVs[3] = tvSpeed15
            TVs[4] = tvSpeed2
        }
    }

    private fun setSpeedBtnClickListeners() {
        val speeds = listOf(-2, -1, 0, 1, 2)
        val rawResources = listOf(R.raw.minustwo, R.raw.minusone, R.raw.zero, R.raw.one, R.raw.two)
        val layouts = CLs

        for (i in speeds.indices) {
            layouts[i]!!.setOnClickListener {
                startPlayer(speeds[i], rawResources[i])
                setSpeedColor(speeds[i])
                flowControllerViewModel.setSpeed(speeds[i])
            }
        }
    }

    fun startPlayer(speed: Int, raw: Int){
        stopMediaPlayers()
        mediaPlayers[speed+2] = MediaPlayer.create(requireContext(), raw)
        mediaPlayers[speed+2]!!.start()
    }
    fun setSpeedColor(speed : Int){
        val selectedBg= R.drawable.selected_speed
        val unselectedBg = R.drawable.unselected_speed_icon
        val selectColor = resources.getColor(R.color.primary)
        val unselectedColor = resources.getColor(R.color.disabled)

        for(i in 0..4){
            CLs[i]!!.setBackgroundResource(unselectedBg)
            TVs[i]!!.setTextColor(unselectedColor)
        }
        CLs[speed + 2]!!.setBackgroundResource(selectedBg)
        TVs[speed + 2]!!.setTextColor(selectColor)
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "발표 연습"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "4/5"
            }
            toolbar.buttonBack.setOnClickListener {
                stopMediaPlayers()
                findNavController().popBackStack()
            }
        }
    }
    private fun stopMediaPlayers() {
        for (player in mediaPlayers) {
                player?.stop()
        }
    }

    override fun onStart() {
        super.onStart()
        animationDrawable.start()
    }

    override fun onStop() {
        super.onStop()
        animationDrawable.stop()
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    //기기의 뒤로가기 버튼을 누를 시
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                stopMediaPlayers()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}