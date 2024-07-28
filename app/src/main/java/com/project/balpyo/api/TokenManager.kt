package com.project.balpyo.api

import android.content.Context
import android.content.SharedPreferences

class TokenManager(val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)

    fun saveUid(uid: String) {
        val editor = sharedPreferences.edit()
        editor.putString("uid", uid)
        editor.apply()
    }

    fun getUid(): String? {
        return sharedPreferences.getString("uid", null)
    }

    // UID 삭제
    fun deleteUid() {
        val sharedPreferences = context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("uid")
        editor.apply()
    }

    fun saveToken(token: String){
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }
    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun deleteToken() {
        val sharedPreferences = context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.apply()
    }

}