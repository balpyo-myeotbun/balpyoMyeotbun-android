package com.project.balpyo.Storage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistoryManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("history", Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private val gson = Gson()

    fun saveSearchQuery(query: String) {
        val searchHistory = getSearchHistory().toMutableList()

        // 중복된 검색어가 있는 경우 제거
        searchHistory.remove(query)

        // 새로운 검색어를 맨 앞에 추가
        searchHistory.add(0, query)

        // 최대 10개의 검색어로 제한
        if (searchHistory.size > 10) {
            searchHistory.removeAt(searchHistory.size - 1)
        }

        // 검색 기록을 JSON으로 변환하여 저장
        val json = gson.toJson(searchHistory)
        editor.putString("history", json)
        editor.apply()
    }

    fun getSearchHistory(): List<String> {
        val json = prefs.getString("history", null)
        val type = object : TypeToken<List<String>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun removeSearchQuery(query: String) {
        val searchHistory = getSearchHistory().toMutableList()
        if (searchHistory.contains(query)) {
            searchHistory.remove(query)
            val json = gson.toJson(searchHistory)
            editor.putString("history", json)
            editor.apply()
        }
    }

    fun clearAllSearchHistory() {
        editor.remove("history")
        editor.apply()
    }
}

