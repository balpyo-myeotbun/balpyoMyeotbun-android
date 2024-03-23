package com.project.balpyo.FlowController

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.response.GenerateAudioResponse
import com.project.balpyo.databinding.FragmentFlowControllerPreviewBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

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
        binding.FCPScript.movementMethod = ScrollingMovementMethod.getInstance()
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

        binding.FCPScript.text = spannable
        Log.d("script", flowControllerViewModel.getCustomScriptData().value.toString())

        binding.FCPNextBtn.setOnClickListener {
            generateAudio(requireActivity())
            /*if(flowControllerViewModel.getIsEditData().value == true){
                //스크립트 수정
            }*/
        }
        binding.FCPEditBtn.setOnClickListener {
            findNavController().navigate(R.id.flowControllerEditScriptFragment)
        }
        return binding.root
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "4/5"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }



    //TTS 받아오기 테스트
    fun generateAudio(requireActivity: FragmentActivity) {
        findNavController().navigate(R.id.loadingFragment)
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        val request = GenerateAudioRequest(flowControllerViewModel.getCustomScriptData().value.toString(), 0, "1234")
        apiClient.apiService.generateAudio("audio/mp3", request)?.enqueue(object :
            Callback<GenerateAudioResponse> {
            override fun onResponse(call: Call<GenerateAudioResponse>, response: Response<GenerateAudioResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: GenerateAudioResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())
                    flowControllerViewModel.setAudioUrl(result!!.profileUrl)
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