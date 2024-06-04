package com.project.balpyo.Script

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
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
import com.project.balpyo.R
import com.project.balpyo.Script.Data.ScriptResultData
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.StoreScriptRequest
import com.project.balpyo.api.response.StoreScriptResponse
import com.project.balpyo.databinding.FragmentScriptResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ScriptResultFragment : Fragment() {

    lateinit var binding: FragmentScriptResultBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: GenerateScriptViewModel

    var editable = false
    var scriptGptId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScriptResultBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        //알림 클릭 후 펜딩인텐트로 메인 액티비티에서 넘어왔을 시 수행 코드
        //현재 펜딩인텐트 관련 코드 주석처리로 동작하지 않음
        arguments?.let { bundle ->
            viewModel = ViewModelProvider(mainActivity)[GenerateScriptViewModel::class.java]
            val data = bundle.getSerializable("data") as ScriptResultData // "key"를 사용하여 데이터를 가져옴
            binding.editTextScript.setText(data.script)
            scriptGptId = data.gptId
            initToolBar()
            binding.textViewEdit.visibility = View.INVISIBLE
            binding.editTextScript.isFocusableInTouchMode = false
            var minute = (data.secTime.toInt()) / 60
            var second = (data.secTime.toInt()) % 60

            binding.textViewGoalTime.text = "${minute}분 ${second}초에 맞는"
            binding.textViewSuccess.text = "${data.title} 대본이\n완성되었어요!"

            var fullText = binding.textViewSuccess.text

            val spannableString = SpannableString(fullText)

            // 시작 인덱스와 끝 인덱스 사이의 텍스트에 다른 색상 적용
            val startIndex = 0
            val endIndex = data.title.length
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#EB2A63")), // 색상 설정
                startIndex,
                endIndex,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )// 스타일 적용 범위 설정
            binding.textViewSuccess.text = spannableString
            binding.buttonStore.setOnClickListener {
                storeScript(data)
                var navHostFragment = mainActivity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                var navController = navHostFragment.navController
                navController.navigate(R.id.homeFragment)
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestination, true) // 스택의 처음부터 현재 위치까지 모두 팝
                    .build()
                navController.navigate(R.id.homeFragment, null, navOptions)
            }
        } ?: run {
            //기존 코드
            viewModel = ViewModelProvider(mainActivity)[GenerateScriptViewModel::class.java]
            viewModel.run {
                script.observe(mainActivity) {
                    binding.editTextScript.setText(it)
                    Log.d("발표몇분", "${it}")
                }
                gptId.observe(mainActivity) {
                    scriptGptId = it.toString()
                    Log.d("발표몇분", "${scriptGptId}")
                }
            }
            initToolBar()
            binding.textViewEdit.visibility = View.INVISIBLE
            binding.editTextScript.isFocusableInTouchMode = false

            binding.run {
                var minute = (MyApplication.scrpitTime.toInt()) / 60
                var second = (MyApplication.scrpitTime.toInt()) % 60

                textViewGoalTime.text = "${minute}분 ${second}초에 맞는"
                textViewSuccess.text = "${MyApplication.scriptTitle} 대본이\n완성되었어요!"

                var fullText = textViewSuccess.text

                val spannableString = SpannableString(fullText)

                val startIndex = 0
                val endIndex = MyApplication.scriptTitle.length
                spannableString.setSpan(
                    ForegroundColorSpan(Color.parseColor("#EB2A63")), // 색상 설정
                    startIndex, // 시작 인덱스
                    endIndex, // 끝 인덱스 (exclusive)
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE // 스타일 적용 범위 설정
                )

                textViewSuccess.text = spannableString
                buttonStore.setOnClickListener {
                    storeScript()
                }
            }
        }

        binding.run {
            buttonEdit.setOnClickListener {
                if (editable) {
                    buttonEdit.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.white)
                    buttonEdit.text = "대본 수정하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray3))
                    textViewEdit.visibility = View.INVISIBLE
                    editTextScript.isFocusableInTouchMode = false
                } else {
                    buttonEdit.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.gray2)
                    buttonEdit.text = "대본 수정 완료"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                    textViewEdit.visibility = View.VISIBLE
                    editTextScript.isFocusableInTouchMode = true
                }
                editable = !editable
            }

            editTextScript.setOnLongClickListener {
                copyStr(requireContext(), editTextScript.text.toString())
            }

            buttonStore.setOnClickListener {
                storeScript()
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
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                /*val transaction: FragmentTransaction =
                    mainActivity!!.supportFragmentManager.beginTransaction()
                val homeFragment = HomeFragment()
                transaction.replace(R.id.fragmentContainerView, homeFragment)
                transaction.commit()*/
                /*var navHostFragment = mainActivity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                var navController = navHostFragment.navController
                navController.navigate(R.id.homeFragment)*/
                findNavController().popBackStack()
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
                findNavController().popBackStack()
            }
        }
    }

    fun storeScript() {
        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        var inputScriptInfo = StoreScriptRequest(binding.editTextScript.text.toString(), scriptGptId, MyApplication.scriptTitle, MyApplication.scrpitTime)
        Log.d("##", "script info : ${inputScriptInfo}")

        apiClient.apiService.storeScript("${tokenManager.getUid()}",inputScriptInfo)?.enqueue(object :
            Callback<StoreScriptResponse> {
            override fun onResponse(call: Call<StoreScriptResponse>, response: Response<StoreScriptResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: StoreScriptResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())
                    var navHostFragment = mainActivity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                    var navController = navHostFragment.navController
                    navController.navigate(R.id.homeFragment)
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(navController.graph.startDestination, true) // 스택의 처음부터 현재 위치까지 모두 팝
                        .build()

                    navController.navigate(R.id.homeFragment, null, navOptions)

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
    fun storeScript(data : ScriptResultData) {
        var apiClient = ApiClient(mainActivity)

        var inputScriptInfo = StoreScriptRequest(binding.editTextScript.text.toString(), data.gptId, data.title, data.secTime)
        Log.d("##", "script info : ${inputScriptInfo}")

        apiClient.apiService.storeScript(data.uid,inputScriptInfo)?.enqueue(object :
            Callback<StoreScriptResponse> {
            override fun onResponse(call: Call<StoreScriptResponse>, response: Response<StoreScriptResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: StoreScriptResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

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