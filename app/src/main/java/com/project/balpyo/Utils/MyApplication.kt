package com.project.balpyo.Utils

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.project.balpyo.api.response.SpeechMark

class MyApplication : Application() {
    companion object {

        lateinit var preferences: PreferenceUtil

        // 1. 발표 시간 맞춤형 스크립트 생성
        var scriptGenerating = false
        var scriptId = 0L
        var scriptTitle = ""
        var scriptTopic = ""
        var scriptSubtopic = ""
        var scriptTime = 180L
        var scriptTimeString = ""

        // 2.  단어당 발표 시간 계산기(관리기)
        var timeCalculatorTitle = ""
        var timeCalculatorScript = ""
        var timeCalculatorTime = 0L
        var timeCalculatorSpeed = "0"
        var calculatedTimeMinute = 0
        var calculatedTimeSecond = 0
        var calculatedTime = 0L
        lateinit var speechMarks : List<SpeechMark>

        //이메일 회원가입
        var email = ""
        var password = ""

        // FCM
        var type = ""
        var mainActivity = false
    }
}