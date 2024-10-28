package com.project.balpyo.FlowController.ViewModel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.project.balpyo.FlowController.FlowControllerPreviewFragmentDirections
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.data.Tag
import com.project.balpyo.api.request.EditScriptRequest
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.request.GenerateNoteRequest
import com.project.balpyo.api.response.EditScriptResponse
import com.project.balpyo.api.response.GenerateAudioResponse
import com.project.balpyo.api.response.SpeechMark
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FlowControllerViewModel : ViewModel() {

    private var title: MutableLiveData<String> = MutableLiveData()
    private var normalScript: MutableLiveData<String> = MutableLiveData("")
    private var customScript: MutableLiveData<String> = MutableLiveData()
    private var serviceCustomScript: MutableLiveData<String> = MutableLiveData()
    private var splitScriptToSentences: MutableLiveData<List<String>> = MutableLiveData()
    private var speed: MutableLiveData<Int> = MutableLiveData(0)
    private var isEdit : MutableLiveData<Boolean> = MutableLiveData(false)
    private var scriptId : MutableLiveData<Long> = MutableLiveData()
    private var audioUrl : MutableLiveData<String> = MutableLiveData()
    private var speechMarks : MutableLiveData<List<SpeechMark>> = MutableLiveData()
    var generateAudioResponse : MutableLiveData<GenerateAudioResponse> = MutableLiveData()
    var generateNoteResponse : MutableLiveData<BaseDto> = MutableLiveData()

    fun initialize() {
        title = MutableLiveData()
        normalScript = MutableLiveData("")
        customScript = MutableLiveData()
        serviceCustomScript = MutableLiveData()
        splitScriptToSentences = MutableLiveData()
        speed = MutableLiveData(0)
        isEdit = MutableLiveData(false)
        scriptId = MutableLiveData()
        audioUrl = MutableLiveData()
        speechMarks = MutableLiveData()
    }
    fun editScript(mainActivity: MainActivity) {
        val apiClient = ApiClient(mainActivity)
            getAudioDuration(getAudioUrlData().value!!, { duration ->
                val durationInSeconds = duration / 1000
                val editScript = EditScriptRequest(getScriptIdData().value!!, getCustomScriptData().value!!, getTitleData().value!!, durationInSeconds.toLong())
                apiClient.apiService.editScript("Bearer ${PreferenceHelper.getUserToken(mainActivity)}",getScriptIdData().value!!.toInt(), editScript)
                    .enqueue(object :
                        Callback<EditScriptResponse> {
                        override fun onResponse(call: Call<EditScriptResponse>, response: Response<EditScriptResponse>) {
                            if (response.isSuccessful) {
                                // 정상적으로 통신이 성공된 경우
                                val result: EditScriptResponse? = response.body()
                                Log.d("##", "onResponse 성공: " + result?.toString())

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
            }, { error ->
                error.printStackTrace()
            })
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
                            generateNoteResponse.value = result!!
                            navController.navigate(R.id.flowControllerResultFragment)
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


    fun getAudioDuration(url: String, onDurationRetrieved: (Int) -> Unit, onError: (Exception) -> Unit) {
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener {
                val duration = it.duration
                onDurationRetrieved(duration)
                it.release()
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                onError(IOException("Failed to load media"))
                true
            }
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            onError(e)
        }
    }

    fun getTitleData(): MutableLiveData<String> = title
    fun getNormalScriptData(): MutableLiveData<String> = normalScript
    fun getCustomScriptData(): MutableLiveData<String> = customScript
    fun getServiceCustomScriptData(): MutableLiveData<String> = serviceCustomScript
    fun getSpeedData(): MutableLiveData<Int> = speed
    fun getIsEditData():MutableLiveData<Boolean> = isEdit

    fun getScriptIdData():MutableLiveData<Long> = scriptId

    fun getSplitScriptToSentencesData() : MutableLiveData<List<String>> = splitScriptToSentences
    fun getAudioUrlData() : MutableLiveData<String> = audioUrl
    fun getSpeechMarks() : MutableLiveData<List<SpeechMark>> = speechMarks
    fun setTitle(text: String) {
        title.value = text
    }
    fun setNormalScript(text: String) {
        normalScript.value = text
    }
    fun setCustomScript(text: String) {
        customScript.value = text
    }
    fun setServiceCustomScript(text: String) {
        serviceCustomScript.value = text
    }

    fun setSplitScriptToSentences(list: List<String>){
        splitScriptToSentences.value = list
    }

    fun setSpeed(value: Int){
        speed.value = value
    }
    fun setIsEdit(value: Boolean){
        isEdit.value = value
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