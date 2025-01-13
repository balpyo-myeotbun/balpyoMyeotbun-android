package com.project.balpyo.Script

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.BottomSheet.FlowControllerEditBottomSheetFragment
import com.project.balpyo.FlowController.BottomSheet.FlowControllerEditBottomSheetListener
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.request.EditScriptRequestWithCalc
import com.project.balpyo.databinding.FragmentScriptEditBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScriptEditFragment : Fragment(), FlowControllerEditBottomSheetListener {

    lateinit var binding: FragmentScriptEditBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: StorageViewModel

    var generatedScriptResult = BaseDto(
        id = null,
        content = null,
        title = null,
        secTime = null,
        voiceFilePath = null,
        isGenerating = null,
        playTime = null,
        originalScript = null,
        speed = null,
        useAi = null,
        tags = null,
        topic = null,
        keywords = null,
        fcmToken = null,
        speechMark = null
    )

    var bottomSheet = FlowControllerEditBottomSheetFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScriptEditBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]

        viewModel.run {
            storageDetail.observe(viewLifecycleOwner) {
                binding.run {
                    if (it != null) {
                        editTextScript.setText(it.content)
                        toolbar.textViewTitle.text = it.title
                        textViewScriptTitle.text = it.title
                        textViewGoalTime.text = "${(it.secTime!! / 60)}분 ${it.secTime!! % 60}초"
                    }
                }
            }
        }

        initView()

        return binding.root
    }

    fun editTime() {
        val apiClient = ApiClient(mainActivity)

        val request = EditScriptRequestWithCalc(
            binding.toolbar.textViewTitle.text.toString(),
            binding.editTextScript.text.toString(),
            generatedScriptResult.speed!!
        )
        apiClient.apiService.editAndCalc(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)!!}",
            generatedScriptResult.id!!,
            request
        ).enqueue(object :
            Callback<BaseDto> {
            override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: BaseDto? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    convertMsToMinutesSeconds((result?.playTime?.toLong())?.times(1000) ?: 0)
                    MyApplication.speechMarks = result?.speechMark ?: emptyList()
                    Log.d("##", "duration : ${MyApplication.calculatedTimeMinute}분 ${MyApplication.calculatedTimeSecond}초")

                    binding.run {
                        textViewSuccess.text = "대본이 완성되었어요!"
                        editTextScript.isFocusableInTouchMode = false
                        toolbar.run {
                            imageViewButtonEditMenu.visibility = View.VISIBLE
                        }
                    }


                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: BaseDto? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<BaseDto>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
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

        MyApplication.calculatedTime = totalSeconds

        return String.format("%02d:%02d", minutes, seconds)
    }

    fun initView() {

        binding.run {
            editTextScript.isFocusableInTouchMode = false

            toolbar.run {
                imageViewButtonBack.visibility = View.VISIBLE
                imageViewButtonClose.visibility = View.INVISIBLE
                textViewPage.visibility = View.INVISIBLE
                imageViewButtonEditMenu.visibility = View.VISIBLE
                textViewTitle.visibility = View.VISIBLE
                textViewTitle.text = "${generatedScriptResult.title}"
                imageViewButtonBack.setOnClickListener {
                    // 뒤로가기 버튼 클릭시 동작
                    findNavController().popBackStack()
                }
                imageViewButtonEditMenu.setOnClickListener {
//                    val bottomSheetFragment = FlowControllerEditBottomSheetFragment()
//                    bottomSheetFragment.show(
//                        mainActivity.supportFragmentManager,
//                        bottomSheetFragment.tag
//                    )
                    bottomSheet.show(childFragmentManager, bottomSheet.tag)
                }
                textViewButtonStore.setOnClickListener {
                    textViewButtonStore.visibility = View.INVISIBLE
                    editTime()
                }
            }
        }
    }

    override fun onItemSelected(position: Int) {
        if(position == 0) {
            // position == 0 : 삭제

        } else {
            // position == 1 : 수정
            binding.run {
                editTextScript.isFocusableInTouchMode = true
                textViewSuccess.text = "대본을 수정해주세요"
                toolbar.run {
                    imageViewButtonEditMenu.visibility = View.INVISIBLE
                    textViewButtonStore.visibility = View.VISIBLE
                }
            }
        }
    }
}