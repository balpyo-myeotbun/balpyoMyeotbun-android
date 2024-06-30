package com.project.balpyo.TimeCalculator

import android.content.ContentValues.TAG
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.FlowControllerPreviewFragmentDirections
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.LoadingFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
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

    lateinit var flowControllerViewModel: FlowControllerViewModel

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
        mainActivity = activity as MainActivity

        flowControllerViewModel = ViewModelProvider(mainActivity)[FlowControllerViewModel::class.java]

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
                            MyApplication.timeCalculatorSpeed = "-2"
                            mediaPlayerMinusTwo = MediaPlayer.create(requireContext(), R.raw.minustwo)
                            mediaPlayerMinusOne.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerMinusTwo.start()
                            Log.d("발표몇분","-2")
                        }
                        "-1" -> {
                            MyApplication.timeCalculatorSpeed = "-1"
                            mediaPlayerMinusOne = MediaPlayer.create(requireContext(), R.raw.minusone)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerMinusOne.start()
                        }
                        "0" -> {
                            MyApplication.timeCalculatorSpeed = "0"
                            mediaPlayerZero = MediaPlayer.create(requireContext(), R.raw.zero)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerMinusOne.stop()
                            mediaPlayerOne.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerZero.start()
                        }
                        "1" -> {
                            MyApplication.timeCalculatorSpeed = "1"
                            mediaPlayerOne = MediaPlayer.create(requireContext(), R.raw.one)
                            mediaPlayerMinusTwo.stop()
                            mediaPlayerMinusOne.stop()
                            mediaPlayerZero.stop()
                            mediaPlayerTwo.stop()
                            mediaPlayerOne.start()
                        }
                        "2" -> {
                            MyApplication.timeCalculatorSpeed = "2"
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
                mediaPlayerMinusTwo.stop()
                mediaPlayerMinusOne.stop()
                mediaPlayerZero.stop()
                mediaPlayerOne.stop()
                mediaPlayerTwo.stop()
                generateAudio(mainActivity)
//                findNavController().navigate(R.id.loadingFragment)
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
//                    flowControllerViewModel.setAudioUrl(result!!.profileUrl)
//
//                    var mPlayer = MediaPlayer()
                    //val uri: Uri = Uri.parse("android.resource://" + activity!!.packageName + "/" + R.raw.speech)
                    //mPlayer.setDataSource(activity!!.applicationContext, uri)
//
//                    mPlayer.setDataSource(result!!.profileUrl)
//                    mPlayer.prepare()
//
//                    var durationTime = mPlayer.duration

                    convertMsToMinutesSeconds((result!!.playTime.toLong())*1000)

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
}