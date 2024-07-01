package com.project.balpyo.TimeCalculator

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.FlowControllerPreviewFragmentDirections
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.response.GenerateAudioResponse
import com.project.balpyo.databinding.FragmentTimeCalculatorSpeedBinding
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TimeCalculatorSpeedFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorSpeedBinding
    lateinit var mainActivity: MainActivity

    private lateinit var callback: OnBackPressedCallback

    private val mediaPlayers: Array<MediaPlayer?> = arrayOfNulls(5)

    private val CLs: Array<ConstraintLayout?> = arrayOfNulls(5)
    private val TVs: Array<TextView?> = arrayOfNulls(5)

    private lateinit var animationDrawable: AnimationDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimeCalculatorSpeedBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        animationDrawable = binding.imageViewVolume.drawable as AnimationDrawable

        initToolBar()
        initailizeMediaPlayers()
        initailizeCLsAndTVs()

        MyApplication.timeCalculatorSpeed = "0"
        startPlayer(0, R.raw.zero)
        setSpeedColor(0)

        setSpeedBtnClickListeners()

        binding.run {
            buttonNext.setOnClickListener {
                stopMediaPlayers()
                generateAudio(mainActivity)
            }
        }
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "시간 계산"
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

    //TTS 받아오기 테스트
    fun generateAudio(requireActivity: FragmentActivity) {
        val action = TimeCalculatorSpeedFragmentDirections.actionTimeCalculatorSpeedFragmentToLoadingFragment(
            toolbarTitle = "시간 계산",
            comment = "발표 시간을 계산하고 있어요"
        )
        findNavController().navigate(action)
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        val request = GenerateAudioRequest(MyApplication.timeCalculatorScript, MyApplication.timeCalculatorSpeed.toInt(), "1234")
        apiClient.apiService.generateAudio("audio/mp3", request)?.enqueue(object :
            Callback<GenerateAudioResponse> {
            override fun onResponse(call: Call<GenerateAudioResponse>, response: Response<GenerateAudioResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: GenerateAudioResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    convertMsToMinutesSeconds((result!!.playTime.toLong())*1000)
                    MyApplication.speechMarks = result.speechMarks
                    Log.d("##", "duration : ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초")

                    findNavController().navigate(R.id.timeCalculatorResultFragment)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: GenerateAudioResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<GenerateAudioResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    //밀리 초를 mm:ss로 변환
    fun convertMsToMinutesSeconds(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        MyApplication.calculatedTimeMinute = minutes.toInt()
        MyApplication.calculatedTimeSecond = seconds.toInt()

        MyApplication.calculatedTime = totalSeconds.toLong()

        return String.format("%02d:%02d", minutes, seconds)
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
            CLs[0] = btn03
            CLs[1] = btn05
            CLs[2] = btn1
            CLs[3] = btn15
            CLs[4] = btn2

            TVs[0] = textView03
            TVs[1] = textView05
            TVs[2] = textView1
            TVs[3] = textView15
            TVs[4] = textView2
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
                MyApplication.timeCalculatorSpeed = speeds[i].toString()
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