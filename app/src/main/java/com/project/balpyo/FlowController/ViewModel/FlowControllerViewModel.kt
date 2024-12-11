package com.project.balpyo.FlowController.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.project.balpyo.FlowController.FlowControllerPreviewFragmentDirections
import com.project.balpyo.LoadingFragmentDirections
import com.project.balpyo.MainActivity
import com.project.balpyo.StorageEditFlowControllerTimeFragmentDirections
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.request.EditScriptRequestWithCalc
import com.project.balpyo.api.request.GenerateTimeAndFlowRequest
import com.project.balpyo.api.response.SpeechMark
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlowControllerViewModel : ViewModel() {

    private var title: MutableLiveData<String> = MutableLiveData()
    private var normalScript: MutableLiveData<String> = MutableLiveData("")
    private var customScript: MutableLiveData<String> = MutableLiveData()
    private var splitScriptToSentences: MutableLiveData<List<String>> = MutableLiveData()
    private var speed: MutableLiveData<Int> = MutableLiveData(0)
    private var isEdit : MutableLiveData<Boolean> = MutableLiveData(false)
    private var scriptId : MutableLiveData<Long> = MutableLiveData()
    private var audioUrl : MutableLiveData<String> = MutableLiveData()
    private var speechMarks : MutableLiveData<List<SpeechMark>> = MutableLiveData()
    private var flowControllerResult : MutableLiveData<BaseDto> = MutableLiveData()
    var generateAudioResponse : MutableLiveData<BaseDto> = MutableLiveData()
    var generateFlowControllerResponse : MutableLiveData<BaseDto> = MutableLiveData()

    fun initialize() {
        title.value = ""
        normalScript.value = ""
        customScript.value = ""
        splitScriptToSentences.value = listOf()
        speed.value = 0
        isEdit.value = false
        audioUrl.value = ""
        scriptId.value = -1
        speechMarks.value = listOf()
    }

    fun setFlowControllerResult(flowControllerResult: BaseDto){
        this.flowControllerResult.value = flowControllerResult
        setSpeechMarks(flowControllerResult.speechMark ?: emptyList())
        setCustomScript(flowControllerResult.content ?: "")         //TODO: 커스텀 필드 추가 시 추가
        setNormalScript(flowControllerResult.content ?: "")
        setScriptId(flowControllerResult.id ?: 0)
        setAudioUrl(flowControllerResult.voiceFilePath ?: "")
        setSpeed(flowControllerResult.speed ?: 1)
        setTitle(flowControllerResult.title ?: "")
    }

    fun generateFlowController(navController: NavController, mainActivity: MainActivity) {
        val apiClient = ApiClient(mainActivity)
        val request = GenerateTimeAndFlowRequest(
            getTitleData().value.toString(),
            getCustomScriptData().value.toString(),
            getSpeedData().value!!
        )

        val action = FlowControllerPreviewFragmentDirections.actionFlowControllerPreviewFragmentToLoadingFragment(
            toolbarTitle = "발표연습",
            comment = "발표 연습이 만들어지고 있어요"
        )
        navController.navigate(action)
        apiClient.apiService.postTimeAndFlow(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
            request
        ).enqueue(object : Callback<BaseDto> {
            override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                if (response.isSuccessful) {
                    val result: BaseDto? = response.body()
                    setAudioUrl(result?.voiceFilePath ?: "")
                    generateAudioResponse.value = result?.copy()
                    setSpeechMarks(result?.speechMark ?: emptyList())
                    val flowControllerResultAction = LoadingFragmentDirections.actionLoadingFragmentToFlowControllerResultFragment(
                        type = "New"
                    )
                    navController.navigate(flowControllerResultAction)
                } else {
                    Log.d("##", "onResponse 실패: " + response.code())
                }
            }

            override fun onFailure(call: Call<BaseDto>, t: Throwable) {
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }
    fun editScriptAndCalc(mainActivity: MainActivity, navController: NavController) {
        val apiClient = ApiClient(mainActivity)
        val actionToLoading = StorageEditFlowControllerTimeFragmentDirections.actionStorageEditFlowControllerTimeFragmentToLoadingFragment(
            toolbarTitle = "발표연습",
            comment = "발표 연습이 만들어지고 있어요"
        )
        navController.navigate(actionToLoading)
        scriptId.value?.let {
            apiClient.apiService.editAndCalc(
                "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
                it,
                EditScriptRequestWithCalc(
                    content = normalScript.value ?: "",
                    speed = speed.value ?: 0,
                    title = title.value ?: ""
                )
            ).enqueue(object :
                Callback<BaseDto> {
                override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseDto? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())
                        setFlowControllerResult(result!!)
                        val actionToResult = LoadingFragmentDirections.actionLoadingFragmentToFlowControllerResultFragment(
                            type = "Home"
                        )
                        navController.navigate(actionToResult)
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
    }


    fun getTitleData(): MutableLiveData<String> = title
    fun getNormalScriptData(): MutableLiveData<String> = normalScript
    fun getCustomScriptData(): MutableLiveData<String> = customScript
    fun getSpeedData(): MutableLiveData<Int> = speed
    fun getScriptIdData():MutableLiveData<Long> = scriptId

    fun getSplitScriptToSentencesData() : MutableLiveData<List<String>> = splitScriptToSentences
    fun getAudioUrlData() : MutableLiveData<String> = audioUrl
    fun getSpeechMarks() : MutableLiveData<List<SpeechMark>> = speechMarks

    fun getFlowControllerResultData(): MutableLiveData<BaseDto> = flowControllerResult
    fun setTitle(text: String) {
        title.value = text
    }
    fun setNormalScript(text: String) {
        normalScript.value = text
    }
    fun setCustomScript(text: String) {
        customScript.value = text
    }

    fun setSplitScriptToSentences(list: List<String>){
        splitScriptToSentences.value = list
    }

    fun setSpeed(value: Int){
        speed.value = value
    }
    fun setScriptId(value: Long){
        scriptId.value = value
    }
    fun setAudioUrl(value: String){
        audioUrl.value = value
    }
    fun setSpeechMarks(value: List<SpeechMark>){
        speechMarks.value = value
    }
}