package com.project.balpyo.Utils

import android.app.Application

class MyApplication : Application() {
    companion object {


        // 1. 발표 시간 맞춤형 스크립트 생성
        var scriptTitle = ""
        var scriptSubject = ""
        var scriptSubtopic = ""
        var scrpitTime = 180L

        // 2.  단어당 발표 시간 계산기(관리기)
        var timeCalculatorScript = ""
        var timeCalculatorTime = 0L
        var timeCalculatorSpeed = ""
    }
}