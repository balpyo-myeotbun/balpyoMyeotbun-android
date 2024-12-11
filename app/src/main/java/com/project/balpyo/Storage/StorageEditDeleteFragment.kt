package com.project.balpyo.Storage

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.Storage.StorageEditBottomSheet.StorageBottomSheetListener
import com.project.balpyo.Storage.StorageEditBottomSheet.StorageEditBottomSheetFragment
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.request.EditScriptRequestWithUnCalc
import com.project.balpyo.databinding.FragmentStorageEditDeleteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StorageEditDeleteFragment : Fragment(), StorageBottomSheetListener {
    lateinit var binding: FragmentStorageEditDeleteBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: StorageViewModel

    private lateinit var flowControllerViewModel: FlowControllerViewModel

    var bottomSheet = StorageEditBottomSheetFragment()

    var editable = false

    var scriptId = 0L
    var secTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStorageEditDeleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        viewModel.run {
            storageDetail.observe(mainActivity) {
                binding.editTextScript.setText(it?.content.toString())
                binding.toolbar.textViewTitle.text = it?.title.toString()
                binding.textViewScriptTitle.text = it?.title.toString()
                scriptId = it?.id?: -1
                val minute = (it?.secTime ?: 0) / 60
                val second = (it?.secTime ?: 0) % 60

                binding.textViewGoalTime.text = "${minute}분 ${second}초"
            }
        }

        initToolBar()

        binding.editTextScript.keyListener = null
        binding.run {
            buttonStore.visibility = View.INVISIBLE
            buttonMenu.visibility = View.VISIBLE

            editTextScript.isFocusableInTouchMode = false

            buttonMenu.setOnClickListener {
                bottomSheet.show(childFragmentManager,bottomSheet.tag)
            }

            buttonStore.setOnClickListener {
                editScript()
            }
        }
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onNoteSelected(position: Int) {

        var keyListner = binding.editTextScript.keyListener

        if (position == 2) {
            Log.d("발표몇분", "수정")
            binding.buttonStore.visibility = View.VISIBLE
            binding.buttonMenu.visibility = View.GONE

            binding.editTextScript.run {
                isFocusableInTouchMode = true
                isFocusable = true
                inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }

        } else {
            deleteScript()
        }
    }

    fun editScript() {
        val apiClient = ApiClient(mainActivity)

        val editScript = EditScriptRequestWithUnCalc(
            binding.editTextScript.text.toString(),
            binding.toolbar.textViewTitle.text.toString()
        )

        apiClient.apiService.editWithUnCalc(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
            scriptId,
            editScript
        ).enqueue(
            object : Callback<BaseDto> {
                override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseDto? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())

                        binding.run {
                            buttonStore.visibility = View.INVISIBLE
                            buttonMenu.visibility = View.VISIBLE

                            editTextScript.isFocusableInTouchMode = false
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

    private fun deleteScript() {
        val apiClient = ApiClient(mainActivity)
        apiClient.apiService.deleteScript(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
            scriptId.toInt()
        ).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    viewModel.getStorageList(mainActivity)

                    findNavController().popBackStack()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: Void? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }
}