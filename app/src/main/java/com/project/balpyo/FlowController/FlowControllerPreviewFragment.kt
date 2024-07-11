package com.project.balpyo.FlowController

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.LoadingFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.EditScriptRequest
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.response.EditScriptResponse
import com.project.balpyo.api.response.GenerateAudioResponse
import com.project.balpyo.databinding.FragmentFlowControllerPreviewBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FlowControllerPreviewFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var binding:FragmentFlowControllerPreviewBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = activity as MainActivity
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding = FragmentFlowControllerPreviewBinding.inflate(layoutInflater)
        initToolBar()
        binding.tvScript.movementMethod = ScrollingMovementMethod.getInstance()
        val spannable = SpannableStringBuilder(flowControllerViewModel.getCustomScriptData().value)
        val scriptCharSequence: CharSequence = flowControllerViewModel.getCustomScriptData().value!!
        ///숨 고르기랑 ppt 넘김 회색으로 변경
        val patterns = listOf("숨 고르기 \\(1초\\)", "PPT 넘김 \\(2초\\)")
        patterns.forEach { pattern ->
            val regex = Regex(pattern)
            regex.findAll(scriptCharSequence).forEach { matchResult ->
                val start = matchResult.range.first
                val end = matchResult.range.last + 1
                spannable.setSpan(
                    ForegroundColorSpan(Color.GRAY),
                    start,
                    end,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        binding.tvScript.text = spannable
        Log.d("script", flowControllerViewModel.getCustomScriptData().value.toString())

        binding.btnGenerate.setOnClickListener {
            generateAudio(requireActivity())
        }
        binding.btnEdit.setOnClickListener {
            findNavController().popBackStack(R.id.flowControllerEditScriptFragment, false)
        }
        return binding.root
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "발표 연습"
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "4/5"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    fun getAudioDuration(url: String, onDurationRetrieved: (Int) -> Unit, onError: (Exception) -> Unit) {
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener {
                val duration = it.duration
                onDurationRetrieved(duration)
                it.release()
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                onError(IOException("Failed to load media"))
                true
            }
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            onError(e)
        }
    }
    fun editScript() {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)
        flowControllerViewModel.run{
            getAudioDuration(getAudioUrlData().value!!, { duration ->
                val durationInSeconds = duration / 1000
                var editScript = EditScriptRequest(getScriptIdData().value!!, getCustomScriptData().value!!, getTitleData().value!!, durationInSeconds.toLong())
                apiClient.apiService.editScript("${tokenManager.getUid()}",getScriptIdData().value!!.toInt(), editScript)?.enqueue(object :
                    Callback<EditScriptResponse> {
                    override fun onResponse(call: Call<EditScriptResponse>, response: Response<EditScriptResponse>) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            var result: EditScriptResponse? = response.body()
                            Log.d("##", "onResponse 성공: " + result?.toString())

                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            var result: EditScriptResponse? = response.body()
                            Log.d("##", "onResponse 실패")
                            Log.d("##", "onResponse 실패: " + response.code())
                            Log.d("##", "onResponse 실패: " + response.body())
                            val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                            Log.d("##", "Error Response: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<EditScriptResponse>, t: Throwable) {
                        // 통신 실패
                        Log.d("##", "onFailure 에러: " + t.message.toString());
                    }
                })
            }, { error ->
                error.printStackTrace()
            })
        }
    }
    //TTS 받아오기 테스트
    fun generateAudio(requireActivity: FragmentActivity) {
        val action = FlowControllerPreviewFragmentDirections.actionFlowControllerPreviewFragmentToLoadingFragment(
            toolbarTitle = "발표연습",
            comment = "발표 연습이 만들어지고 있어요"
        )
        findNavController().navigate(action)
        val apiClient = ApiClient(mainActivity)

        val request = GenerateAudioRequest(flowControllerViewModel.getCustomScriptData().value.toString(), flowControllerViewModel.getSpeedData().value!!, "1234")
        apiClient.apiService.generateAudio("audio/mp3", request)?.enqueue(object :
            Callback<GenerateAudioResponse> {
            override fun onResponse(call: Call<GenerateAudioResponse>, response: Response<GenerateAudioResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: GenerateAudioResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())
                    flowControllerViewModel.setAudioUrl(result!!.profileUrl)
                    if(flowControllerViewModel.getIsEditData().value == true){
                        //스크립트 수정
                        editScript()
                    }
                    flowControllerViewModel.setSpeechMarks(result.speechMarks)
                    findNavController().navigate(R.id.flowControllerResultFragment)


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
}