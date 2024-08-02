package com.project.balpyo.Script

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.project.balpyo.Home.HomeFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.NotificationActivity
import com.project.balpyo.R
import com.project.balpyo.Script.Data.ScriptResultData
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.Script.ViewModel.ScriptDetailViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.StoreScriptRequest
import com.project.balpyo.api.response.StorageDetailResult
import com.project.balpyo.api.response.StoreScriptResponse
import com.project.balpyo.databinding.FragmentScriptResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ScriptResultFragment : Fragment() {

    lateinit var binding: FragmentScriptResultBinding
    lateinit var notificationActivity: NotificationActivity
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: GenerateScriptViewModel
    lateinit var scriptViewModel: ScriptDetailViewModel

    var editable = false

    var gptId = ""
    var title = ""
    var secTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScriptResultBinding.inflate(layoutInflater)
        notificationActivity = activity as NotificationActivity
        mainActivity = MainActivity()

        scriptViewModel = ViewModelProvider(notificationActivity)[ScriptDetailViewModel::class.java]
        viewModel = ViewModelProvider(notificationActivity)[GenerateScriptViewModel::class.java]

        scriptViewModel.run {
            scriptResult.observe(notificationActivity) {
                gptId = it.gptId.toString()
                title = it.title
                secTime = it.secTime

                binding.run {
                    editTextScript.setText(it.script)
                    var minute = (it.secTime.toInt()) / 60
                    var second = (it.secTime.toInt()) % 60

                    textViewGoalTime.text = "${minute}분 ${second}초"
                    textViewScriptTitle.text = "${it.title}"
                }
                Log.d("발표몇분", "${it}")
            }
        }

        initToolBar()
        binding.editTextScript.isFocusableInTouchMode = false
        MyApplication.scriptGenerating = false

        binding.run {
            /*
            buttonEdit.setOnClickListener {
                if (editable) {
                    buttonEdit.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.white)
                    buttonEdit.text = "대본 수정하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray3))
                    editTextScript.isFocusableInTouchMode = false
                } else {
                    buttonEdit.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.gray2)
                    buttonEdit.text = "대본 수정 완료"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                    editTextScript.isFocusableInTouchMode = true
                }
                editable = !editable
            }
            */

            buttonStore.setOnClickListener {
                storeScript()
            }

            editTextScript.setOnLongClickListener {
                copyStr(requireContext(), editTextScript.text.toString())
            }
        }
        return binding.root
    }

    private fun copyStr(context : Context, str : String): Boolean {
        var clipboardManager : ClipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        var clipData = ClipData.newPlainText("copyScript", str)
        clipboardManager.setPrimaryClip(clipData)

        return true
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.INVISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.run {
                visibility = View.VISIBLE
                text = "대본 생성"
            }

            toolbar.buttonClose.setOnClickListener {
                // 닫기 버튼 클릭시 동작
                /*val transaction: FragmentTransaction =
                    mainActivity!!.supportFragmentManager.beginTransaction()
                val homeFragment = HomeFragment()
                transaction.replace(R.id.fragmentContainerView, homeFragment)
                transaction.commit()*/
                /*var navHostFragment = mainActivity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                var navController = navHostFragment.navController
                navController.navigate(R.id.homeFragment)*/
//                val mainIntent = Intent(notificationActivity, MainActivity::class.java)
//                mainIntent.putExtra("type", "push")
//                startActivity(mainIntent)

                MyApplication.type = "push"

                if(MyApplication.mainActivity) {
                    notificationActivity.finish()
                } else {
                    notificationActivity.finish()
                    val mainIntent = Intent(notificationActivity, MainActivity::class.java)
                    mainIntent.putExtra("type", "push")
                    startActivity(mainIntent)
                }
//                findNavController().popBackStack()
            }
        }
    }

    fun storeScript() {
        var apiClient = ApiClient(notificationActivity)
        var tokenManager = TokenManager(notificationActivity)

        var inputScriptInfo = StoreScriptRequest(binding.editTextScript.text.toString(), gptId.toString(), title.toString(), listOf("SCRIPT"), secTime)
        Log.d("##", "script info : ${inputScriptInfo}")

        apiClient.apiService.storeScript("Bearer ${PreferenceHelper.getUserToken(mainActivity)}",inputScriptInfo)?.enqueue(object :
            Callback<StoreScriptResponse> {
            override fun onResponse(call: Call<StoreScriptResponse>, response: Response<StoreScriptResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: StoreScriptResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    MyApplication.type = "push"

                    if(MyApplication.mainActivity) {
                        notificationActivity.finish()
                    } else {
                        notificationActivity.finish()
                        val mainIntent = Intent(notificationActivity, MainActivity::class.java)
                        mainIntent.putExtra("type", "push")
                        startActivity(mainIntent)
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: StoreScriptResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<StoreScriptResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}