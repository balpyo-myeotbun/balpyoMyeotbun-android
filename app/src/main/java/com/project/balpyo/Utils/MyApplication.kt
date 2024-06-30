package com.project.balpyo.Utils

import android.app.Application

class MyApplication : Application() {
    companion object {


        // 1. 발표 시간 맞춤형 스크립트 생성
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
    }
}