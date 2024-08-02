package com.project.balpyo.Storage

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Storage.NoteBottomSheet.NoteBottomSheetFragment
import com.project.balpyo.Storage.NoteBottomSheet.NoteBottomSheetListener
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.EditScriptRequest
import com.project.balpyo.api.request.ManageScriptRequest
import com.project.balpyo.api.response.EditScriptResponse
import com.project.balpyo.api.response.ManageScriptResponse
import com.project.balpyo.databinding.FragmentNoteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoteFragment : Fragment(), NoteBottomSheetListener {

    lateinit var binding: FragmentNoteBinding
    lateinit var mainActivity: MainActivity

    var bottomSheet = NoteBottomSheetFragment()

    var scriptId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNoteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()

        binding.run {
            editTextTitle.addTextChangedListener {
                checkText()
            }

            editTextNote.addTextChangedListener {
                checkText()
            }

            buttonStore.setOnClickListener {
                if(scriptId != -1) {
                    editScript()
                } else {
                    storeScript()
                }
            }

            buttonMenu.setOnClickListener {
                bottomSheet.show(childFragmentManager,bottomSheet.tag)
            }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            binding.buttonStore.visibility = View.VISIBLE
            binding.buttonMenu.visibility = View.GONE

            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }


    fun checkText() {
        binding.run {
            if(editTextTitle.text.isNotEmpty() && editTextNote.text.isNotEmpty()) {
                buttonStore.setTextColor(ContextCompat.getColor(mainActivity, R.color.text))
            }
            else {
                buttonStore.setTextColor(ContextCompat.getColor(mainActivity, R.color.disabled))
            }
        }
    }

    // 빈 스크립트 생성
    fun storeScript() {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        var manageScript = ManageScriptRequest("${binding.editTextNote.text}", "${binding.editTextTitle.text}", 0, false)

        apiClient.apiService.manageScript("${tokenManager.getUid()}",manageScript)?.enqueue(object :
            Callback<ManageScriptResponse> {
            override fun onResponse(call: Call<ManageScriptResponse>, response: Response<ManageScriptResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: ManageScriptResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    scriptId = result?.result?.scriptId!!.toInt()

                    Toast.makeText(mainActivity, "저장되었습니다", Toast.LENGTH_SHORT).show()
                    binding.buttonStore.visibility = View.GONE
                    binding.buttonMenu.visibility = View.VISIBLE
                    binding.run {
                        editTextNote.run {
                            isEnabled = false
                            isFocusable = false
                            isFocusableInTouchMode = false
                        }
                        editTextTitle.run {
                            isEnabled = false
                            isFocusable = false
                            isFocusableInTouchMode = false
                        }
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: ManageScriptResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<ManageScriptResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    fun editScript() {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        var editScript = EditScriptRequest(scriptId.toLong(), binding.editTextNote.text.toString(), binding.editTextTitle.text.toString(), 0)

        apiClient.apiService.editScript("${tokenManager.getUid()}",scriptId, editScript)?.enqueue(object :
            Callback<EditScriptResponse> {
            override fun onResponse(call: Call<EditScriptResponse>, response: Response<EditScriptResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: EditScriptResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    Toast.makeText(mainActivity, "저장되었습니다", Toast.LENGTH_SHORT).show()
                    binding.buttonStore.visibility = View.GONE
                    binding.buttonMenu.visibility = View.VISIBLE
                    binding.run {
                        editTextNote.run {
                            isEnabled = false
                            isFocusable = false
                            isFocusableInTouchMode = false
                        }
                        editTextTitle.run {
                            isEnabled = false
                            isFocusable = false
                            isFocusableInTouchMode = false
                        }
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
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    override fun onNoteSelected(position: Int) {
        if (position == 2) {
            Toast.makeText(mainActivity, "수정 후 저장하기를 눌러주세요", Toast.LENGTH_SHORT).show()
            binding.buttonStore.visibility = View.VISIBLE
            binding.buttonMenu.visibility = View.GONE
            binding.editTextNote.run {
                isEnabled = true
                isFocusable = true
                isFocusableInTouchMode = true
            }
            binding.editTextTitle.run {
                isEnabled = true
                isFocusable = true
                isFocusableInTouchMode = true
            }
        } else {
            deleteScript()
        }
    }
}