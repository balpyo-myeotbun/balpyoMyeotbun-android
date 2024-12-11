package com.project.balpyo.Storage

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
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.request.EditScriptRequestWithUnCalc
import com.project.balpyo.api.request.GenerateNoteRequest
import com.project.balpyo.databinding.FragmentNoteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoteFragment : Fragment(), NoteBottomSheetListener {

    lateinit var binding: FragmentNoteBinding
    lateinit var mainActivity: MainActivity

    private var bottomSheet = NoteBottomSheetFragment()

    var scriptId : Long = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
                if(scriptId.toInt() != -1) {
                    editScript()
                } else {
                    generateNote()
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


    private fun checkText() {
        binding.run {
            if(editTextTitle.text.isNotEmpty() && editTextNote.text.isNotEmpty()) {
                buttonStore.setTextColor(ContextCompat.getColor(mainActivity, R.color.text))
            }
            else {
                buttonStore.setTextColor(ContextCompat.getColor(mainActivity, R.color.disabled))
            }
        }
    }

    fun generateNote() {
        val apiClient = ApiClient(mainActivity)

        val generateNoteRequest = GenerateNoteRequest(
            title = "${binding.editTextNote.text}",
            content = "${binding.editTextTitle.text}"
        )

        apiClient.apiService.generateNote(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
            generateNoteRequest
        ).enqueue(object :
            Callback<BaseDto> {
            override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: BaseDto? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    scriptId = result?.id ?: -1

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

    private fun editScript() {
        val apiClient = ApiClient(mainActivity)

        val editScript = EditScriptRequestWithUnCalc(
            binding.editTextNote.text.toString(),
            binding.editTextTitle.text.toString()
        )
        apiClient.apiService.editWithUnCalc(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
            scriptId,
            editScript
        ).enqueue(object :
            Callback<BaseDto> {
            override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: BaseDto? = response.body()
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
            scriptId.toInt()).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    Log.d("##", "onResponse 성공")

                    findNavController().popBackStack()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
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