package com.project.balpyo.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.EditScriptRequest
import com.project.balpyo.api.response.EditScriptResponse
import com.project.balpyo.api.response.VerifyUidResponse
import com.project.balpyo.databinding.FragmentStorageEditDeleteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StorageEditDeleteFragment : Fragment() {
    lateinit var binding: FragmentStorageEditDeleteBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: StorageViewModel

    private lateinit var flowControllerViewModel: FlowControllerViewModel

    var editable = false

    var scriptId = 0L
    var secTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStorageEditDeleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        viewModel.run {
            storageDetail.observe(mainActivity) {
                binding.editTextScript.setText(it.script.toString())
                binding.toolbar.textViewTitle.text = it.title.toString()
                scriptId = it.scriptId.toLong()
                secTime = it.secTime.toLong()
            }
        }

        initToolBar()

        var keyListner = binding.editTextScript.keyListener
        binding.editTextScript.keyListener = null
        binding.run {
            buttonEdit.setOnClickListener {
                if(editable) {
                    if(flowControllerViewModel.getIsEditData().value == true)
                    {
                        flowControllerViewModel.setNormalScript(viewModel.storageDetail.value?.script!!)
                        flowControllerViewModel.setTitle(viewModel.storageDetail.value?.title!!)
                        flowControllerViewModel.setScriptId(viewModel.storageDetail.value?.scriptId!!)
                        findNavController().navigate(R.id.flowControllerEditScriptFragment)
                    }
                    else
                        editScript()
                } else {
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray4)
                    buttonEdit.text = "대본 저장하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    editTextScript.keyListener = keyListner
                }
                editable = !editable
            }

            buttonDelete.setOnClickListener {
                deleteScript()
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

    fun editScript() {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        var editScript = EditScriptRequest(scriptId, binding.editTextScript.text.toString(), binding.toolbar.textViewTitle.text.toString(), secTime)

        apiClient.apiService.editScript("${tokenManager.getUid()}",scriptId.toInt(), editScript)?.enqueue(object :
            Callback<EditScriptResponse> {
            override fun onResponse(call: Call<EditScriptResponse>, response: Response<EditScriptResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: EditScriptResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    Toast.makeText(mainActivity, "대본이 수정됐습니다.", Toast.LENGTH_SHORT).show()

                    binding.run {
                        buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary)
                        buttonEdit.text = "대본 수정하기"
                        buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        editTextScript.keyListener = null
                    }

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
    }

    fun deleteScript() {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.deleteScript("${tokenManager.getUid()}",scriptId.toInt())?.enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: Void? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    viewModel.getStorageList(this@StorageEditDeleteFragment, mainActivity)

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
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }


}