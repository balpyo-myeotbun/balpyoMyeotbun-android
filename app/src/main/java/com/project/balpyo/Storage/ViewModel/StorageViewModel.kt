package com.project.balpyo.Storage.ViewModel

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.balpyo.Storage.LoadScriptBottomSheet.LoadScriptBottomSheetFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.data.Tag
import com.project.balpyo.api.request.SearchParameter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StorageViewModel: ViewModel() {
    var storageList = MutableLiveData<MutableList<BaseDto>>()
    var storageListForBottomSheet = MutableLiveData<MutableList<BaseDto>>()
    var storageDetail = MutableLiveData<BaseDto?>()
    var storageDetailForBottomSheet = MutableLiveData<BaseDto?>()
    var searchList = MutableLiveData<MutableList<BaseDto>>()
    var filterList = MutableLiveData<MutableList<BaseDto>>()


    init {
        storageList.value = mutableListOf()
        searchList.value = mutableListOf()
        filterList.value = mutableListOf()
    }

    fun getStorageList(mainActivity: MainActivity) {

        val tempList = mutableListOf<BaseDto>()

        val apiClient = ApiClient(mainActivity)

        apiClient.apiService.getStorageList("Bearer ${PreferenceHelper.getUserToken(mainActivity)}")
            .enqueue(object :
                Callback<List<BaseDto>> {
                override fun onResponse(call: Call<List<BaseDto>>, response: Response<List<BaseDto>>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: List<BaseDto>? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())

                        if (result != null) {
                            for(item in result) {
                                tempList.add(item)
                            }
                        }

                        storageList.value = tempList

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: List<BaseDto>? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<List<BaseDto>>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun getStorageDetail(mainActivity: MainActivity, scriptId: Long) {

        val apiClient = ApiClient(mainActivity)
        apiClient.apiService.getStorageDetail(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
            scriptId
        ).enqueue(object :
                Callback<BaseDto> {
                override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseDto? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())

                        if (result != null) {
                            storageDetail.value = result
                        }

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseDto? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<BaseDto>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun getStorageListForBottomSheet(fragmentManager: FragmentManager, mainActivity: MainActivity) {

        val tempList = mutableListOf<BaseDto>()

        val apiClient = ApiClient(mainActivity)

        apiClient.apiService.getStorageList("Bearer ${PreferenceHelper.getUserToken(mainActivity)}")
            .enqueue(object :
                Callback<List<BaseDto>> {
                override fun onResponse(call: Call<List<BaseDto>>, response: Response<List<BaseDto>>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: List<BaseDto>? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())

                        if (result != null) {
                            for(item in result) {
                                if(item.tags?.contains(Tag.NOTE.value) == true)
                                    tempList.add(item)
                            }
                        }

                        storageListForBottomSheet.value = tempList

                        val bottomSheetFragment = LoadScriptBottomSheetFragment()
                        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: List<BaseDto>? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<List<BaseDto>>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun getStorageDetailForBottomSheet(mainActivity: MainActivity, scriptId: Long) {

        val apiClient = ApiClient(mainActivity)

        apiClient.apiService.getStorageDetail(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
            scriptId
        ).enqueue(object :
            Callback<BaseDto> {
            override fun onResponse(call: Call<BaseDto>, response: Response<BaseDto>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: BaseDto? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    storageDetailForBottomSheet.value = result
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: BaseDto? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<BaseDto>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    fun deleteScript(mainActivity: MainActivity, id : Long) {
        val apiClient = ApiClient(mainActivity)

        apiClient.apiService.deleteScript("Bearer ${PreferenceHelper.getUserToken(mainActivity)}",id.toInt()).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    val result: Void? = response.body()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: Void? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    fun searchStorageList(mainActivity: MainActivity, searchParameter : SearchParameter) {

        val tempList = mutableListOf<BaseDto>()

        val apiClient = ApiClient(mainActivity)

        apiClient.apiService.search(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
            searchParameter.tag,
            searchParameter.isGenerating,
            searchParameter.searchValue
        ).enqueue(object :
                Callback<List<BaseDto>> {
                override fun onResponse(call: Call<List<BaseDto>>, response: Response<List<BaseDto>>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: List<BaseDto>? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())

                        if (result != null) {
                            for(item in result) {
                                tempList.add(item)
                            }
                        }

                        searchList.value = tempList

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: List<BaseDto>? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<List<BaseDto>>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString())
                }
            })
    }
    fun filterStorageList(mainActivity: MainActivity, searchParameter : SearchParameter) {

        val tempList = mutableListOf<BaseDto>()

        val apiClient = ApiClient(mainActivity)

        apiClient.apiService.search(
            "Bearer ${PreferenceHelper.getUserToken(mainActivity)}",
            searchParameter.tag,
            searchParameter.isGenerating,
            searchParameter.searchValue
        ).enqueue(object :
                Callback<List<BaseDto>> {
                override fun onResponse(call: Call<List<BaseDto>>, response: Response<List<BaseDto>>) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: List<BaseDto>? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())

                        if (result != null) {
                            for(item in result) {
                                tempList.add(item)
                            }
                        }

                        filterList.value = tempList

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: List<BaseDto>? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<List<BaseDto>>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    //바텀시트 뷰모델 초기화
    fun clearValueStorageDataForBottomSheet() {
        storageDetailForBottomSheet.value = BaseDto(
            0,
            "",
            "",
            0,
            "",
            false,
            0,
            "",
            0,
            false,
            listOf(""),
            "",
            "",
            "",
            listOf()
        )
    }
}