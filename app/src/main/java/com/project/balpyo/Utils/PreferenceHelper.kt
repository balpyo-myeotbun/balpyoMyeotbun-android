package com.project.balpyo.Utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    private const val PREFS_NAME = "balpyo_prefs"
    private const val KEY_TOKEN = "key_token"
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_USER_TYPE = "key_user_type"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserToken(context: Context, token: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getUserToken(context: Context): String? {
        val prefs = getPreferences(context)
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearUserToken(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun saveUserId(context: Context, userId: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): String? {
        val prefs = getPreferences(context)
        return prefs.getString(KEY_USER_ID, null)
    }

    fun clearUserId(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    fun saveUserType(context: Context, userType: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(KEY_USER_TYPE, userType).apply()
    }

    fun getUserType(context: Context): String? {
        val prefs = getPreferences(context)
        return prefs.getString(KEY_USER_TYPE, null)
    }

    fun clearUserType(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().remove(KEY_USER_TYPE).apply()
    }

    fun clearAll(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().clear().apply()
    }
}