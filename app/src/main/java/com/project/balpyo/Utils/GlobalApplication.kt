package com.project.balpyo.Utils

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "770cc92a66692776000650cc82b91db9")
    }
}