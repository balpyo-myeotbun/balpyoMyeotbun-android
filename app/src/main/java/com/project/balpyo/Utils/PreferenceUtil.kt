package com.project.balpyo.Utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun setFCMToken(token: String){
        preferences.edit().putString("FCM_TOKEN", token).apply()
    }

    fun getFCMToken(): String? =
        preferences.getString("FCM_TOKEN", null)

    fun setScriptResult(script: String){
        preferences.edit().putString("SCRIPT", script).apply()
    }

    fun getScriptResult(): String? =
        preferences.getString("SCRIPT", null)
}