package com.project.balpyo.FlowController.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FlowControllerViewModel : ViewModel() {

    private var title: MutableLiveData<String> = MutableLiveData()
    private var normalScript: MutableLiveData<String> = MutableLiveData("")
    private var customScript: MutableLiveData<String> = MutableLiveData()
    private var serviceCustomScript: MutableLiveData<String> = MutableLiveData()
    private var splitScriptToSentences: MutableLiveData<List<String>> = MutableLiveData()
    private var speed: MutableLiveData<Int> = MutableLiveData(0)
    private var isEdit : MutableLiveData<Boolean> = MutableLiveData(false)
    private var scriptId : MutableLiveData<String> = MutableLiveData()
    private var audioUrl : MutableLiveData<String> = MutableLiveData()

    fun getTitleData(): MutableLiveData<String> = title
    fun getNormalScriptData(): MutableLiveData<String> = normalScript
    fun getCustomScriptData(): MutableLiveData<String> = customScript
    fun getServiceCustomScriptData(): MutableLiveData<String> = serviceCustomScript
    fun getSpeedData(): MutableLiveData<Int> = speed
    fun getIsEditData():MutableLiveData<Boolean> = isEdit

    fun getScriptIdData():MutableLiveData<String> = scriptId

    fun getSplitScriptToSentencesData() : MutableLiveData<List<String>> = splitScriptToSentences
    fun getAudioUrlData() : MutableLiveData<String> = audioUrl
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

    fun setScriptId(value: String){
        scriptId.value = value
    }
    fun setAudioUrl(value: String){
        audioUrl.value = value
    }
}