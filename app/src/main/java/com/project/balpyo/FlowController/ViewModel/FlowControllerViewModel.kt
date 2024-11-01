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
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.data.Tag
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.request.GenerateNoteRequest
import com.project.balpyo.api.response.GenerateAudioResponse
import com.project.balpyo.api.response.SpeechMark
import com.project.balpyo.api.response.StorageListResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    private var flowControllerResult : MutableLiveData<StorageListResult> = MutableLiveData()
    var generateAudioResponse : MutableLiveData<GenerateAudioResponse> = MutableLiveData()
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

    fun setFlowControllerResult(storageListResult: StorageListResult){
        flowControllerResult.value = storageListResult
        setSpeechMarks(storageListResult.speechMark)
        setCustomScript(storageListResult.content)         //TODO: 커스텀 필드 추가 시 추가
        setNormalScript(storageListResult.content)
        setScriptId(storageListResult.id)
        setAudioUrl(storageListResult.voiceFilePath!!)
        setSpeed(storageListResult.speed.toInt())
        setTitle(storageListResult.title)
    }

    suspend fun generateAudio(navController: NavController, mainActivity: MainActivity): GenerateAudioResponse? {
        val apiClient = ApiClient(mainActivity)
        val request = GenerateAudioRequest(getCustomScriptData().value.toString(), getSpeedData().value!!)

        val action = FlowControllerPreviewFragmentDirections.actionFlowControllerPreviewFragmentToLoadingFragment(
            toolbarTitle = "발표연습",
            comment = "발표 연습이 만들어지고 있어요"
        )
        navController.navigate(action)

        return suspendCoroutine { continuation ->
            apiClient.apiService.generateAudio("Bearer ${PreferenceHelper.getUserToken(mainActivity)}", "audio/mp3", request)
                .enqueue(object : Callback<GenerateAudioResponse> {
                    override fun onResponse(call: Call<GenerateAudioResponse>, response: Response<GenerateAudioResponse>) {
                        if (response.isSuccessful) {
                            val result: GenerateAudioResponse? = response.body()
                            setAudioUrl(result!!.profileUrl)
                            generateAudioResponse.value = result.copy()
                            setSpeechMarks(result.speechMarks)
                            continuation.resume(result) // URL을 반환
                        } else {
                            Log.d("##", "onResponse 실패: " + response.code())
                            continuation.resume(null) // 실패 시 null 반환
                        }
                    }

                    override fun onFailure(call: Call<GenerateAudioResponse>, t: Throwable) {
                        Log.d("##", "onFailure 에러: " + t.message.toString())
                        continuation.resume(null) // 실패 시 null 반환
                    }
                })
        }
    }

    suspend fun generateFlowControllerForStorage(mainActivity: MainActivity, navController: NavController) {
        val generateAudioResponse = generateAudio(navController, mainActivity)

        if (generateAudioResponse != null) {
            val apiClient = ApiClient(mainActivity)
            val tokenManager = TokenManager(mainActivity)
            val generateNoteRequest = GenerateNoteRequest(
                content = getNormalScriptData().value!!,
                title = getTitleData().value!!,
                voiceFilePath = generateAudioResponse.profileUrl,
                originalScript = getNormalScriptData().value!!,
                speed = getSpeedData().value!!.toLong(),
                useAi = true,
                tags = listOf(Tag.FLOW.value),
                speechMark = generateAudioResponse.speechMarks
            )
            Log.d("##", generateNoteRequest.toString())

            apiClient.apiService.generateNote("Bearer ${tokenManager.getToken()!!}", generateNoteRequest)
                .enqueue(object : Callback<BaseDto> {
                    override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                        if (response.isSuccessful) {
                            val result: BaseDto? = response.body()
                            generateFlowControllerResponse.value = result!!
                            val action = LoadingFragmentDirections.actionLoadingFragmentToFlowControllerResultFragment(
                                type = "New"
                            )
                            navController.navigate(action)
                        } else {
                            Log.d("##", "onResponse 실패: " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<BaseDto>, t: Throwable) {
                        Log.d("##", "onFailure 에러: " + t.message.toString())
                    }
                })
        } else {
            Log.d("##", "Audio URL을 가져오는 데 실패했습니다.")
        }
    }
    fun editScriptAndCalc(mainActivity: MainActivity, baseDto: BaseDto, navController: NavController) {
        val apiClient = ApiClient(mainActivity)
        val actionToLoading = StorageEditFlowControllerTimeFragmentDirections.actionStorageEditFlowControllerTimeFragmentToLoadingFragment(
            toolbarTitle = "발표연습",
            comment = "발표 연습이 만들어지고 있어요"
        )
        navController.navigate(actionToLoading)
        apiClient.apiService.editAndCalc("Bearer ${PreferenceHelper.getUserToken(mainActivity)}", scriptId.value!!.toInt(), baseDto )
            .enqueue(object :
                Callback<StorageListResult> {
                override fun onResponse(call: Call<StorageListResult>, response: Response<StorageListResult>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: StorageListResult? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())
                        setFlowControllerResult(result!!)
                        val actionToResult = LoadingFragmentDirections.actionLoadingFragmentToFlowControllerResultFragment(
                            type = "Home"
                        )
                        navController.navigate(actionToResult)
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: StorageListResult? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<StorageListResult>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString());
                }
            })
    }


    fun getTitleData(): MutableLiveData<String> = title
    fun getNormalScriptData(): MutableLiveData<String> = normalScript
    fun getCustomScriptData(): MutableLiveData<String> = customScript
    fun getSpeedData(): MutableLiveData<Int> = speed
    fun getScriptIdData():MutableLiveData<Long> = scriptId

    fun getSplitScriptToSentencesData() : MutableLiveData<List<String>> = splitScriptToSentences
    fun getAudioUrlData() : MutableLiveData<String> = audioUrl
    fun getSpeechMarks() : MutableLiveData<List<SpeechMark>> = speechMarks

    fun getFlowControllerResultData(): MutableLiveData<StorageListResult> = flowControllerResult
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