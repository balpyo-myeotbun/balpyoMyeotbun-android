package com.project.balpyo.FlowController.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FlowControllerViewModel : ViewModel() {

    private var title: MutableLiveData<String> = MutableLiveData()
    private var normalScript: MutableLiveData<String> = MutableLiveData("")
    private var customScript: MutableLiveData<String> = MutableLiveData()
    private var serviceCustomScript: MutableLiveData<String> = MutableLiveData()
    private var splitScriptToSentences: MutableLiveData<List<String>> = MutableLiveData()

    fun getTitleData(): MutableLiveData<String> = title
    fun getNormalScriptData(): MutableLiveData<String> = normalScript
    fun getCustomScriptData(): MutableLiveData<String> = customScript
    fun getServiceCustomScriptData(): MutableLiveData<String> = serviceCustomScript

    fun getSplitScriptToSentencesData() : MutableLiveData<List<String>> = splitScriptToSentences
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
}