package com.project.balpyo.Home.ViewModel

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import com.project.balpyo.BottomSheet.BottomSheetFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.response.StorageDetailResponse
import com.project.balpyo.api.response.StorageDetailResult
import com.project.balpyo.api.response.StorageListResponse
import com.project.balpyo.api.response.StorageListResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StorageViewModel: ViewModel() {
    var storageList = MutableLiveData<MutableList<StorageListResult>>()
    var storageListForBottomSheet = MutableLiveData<MutableList<StorageListResult>>()
    var storageDetail = MutableLiveData<StorageDetailResult>()
    var storageDetailForBottomSheet = MutableLiveData<StorageDetailResult>()


    init {
        storageList.value = mutableListOf<StorageListResult>()
    }

    fun getStorageList(fragment: Fragment, mainActivity: MainActivity) {

        var tempList = mutableListOf<StorageListResult>()

        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.getStorageList("${tokenManager.getUid()}")?.enqueue(object :
            Callback<StorageListResponse> {
            override fun onResponse(call: Call<StorageListResponse>, response: Response<StorageListResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: StorageListResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    for(i in 0 until result?.result!!.size) {
                        var scriptId = result?.result!!.get(i).scriptId
                        var script = ""
                        if(result?.result!!.get(i).script != null) {
                            script = result?.result!!.get(i).script.toString()
                        } else {
                            script = ""
                        }
                        var gptId = ""
                        if(result?.result!!.get(i).gptId != null) {
                            gptId = result?.result!!.get(i).gptId.toString()
                        } else {
                            gptId = ""
                        }
                        var uid = result?.result!!.get(i).uid
                        var title = result?.result!!.get(i).title
                        var secTime = result?.result!!.get(i).secTime
                        var filePath = result?.result!!.get(i).voiceFilePath

                        var s1 = StorageListResult(scriptId, script, gptId, uid, title, secTime, filePath)
                        tempList.add(s1)
                    }

                    storageList.value = tempList

                    NavHostFragment.findNavController(fragment).navigate(R.id.storageFragment)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: StorageListResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    NavHostFragment.findNavController(fragment).popBackStack()
                }
            }

            override fun onFailure(call: Call<StorageListResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());


                NavHostFragment.findNavController(fragment).popBackStack()
            }
        })
    }

    fun getStorageDetail(fragment: Fragment, mainActivity: MainActivity, scriptId: Int) {

        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.getStorageDetail("${tokenManager.getUid()}", scriptId)?.enqueue(object :
            Callback<StorageDetailResponse> {
            override fun onResponse(call: Call<StorageDetailResponse>, response: Response<StorageDetailResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: StorageDetailResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())
                    //ssml 태그를 제외한 일반 스크립트만을 저장하기 위함
                    var normalScript = replaceScriptToNormal(result?.result!!.script)
                    storageDetail.value = StorageDetailResult(result?.result!!.scriptId, normalScript,  result?.result!!.gptId,  result?.result!!.uid,  result?.result!!.title,  result?.result!!.secTime)
                    //NavHostFragment.findNavController(fragment).navigate(R.id.storageEditDeleteFragment)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: StorageDetailResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    NavHostFragment.findNavController(fragment).popBackStack()
                }
            }

            override fun onFailure(call: Call<StorageDetailResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());


                NavHostFragment.findNavController(fragment).popBackStack()
            }
        })
    }

    fun getStorageListForBottomSheet(fragmentManager: FragmentManager, mainActivity: MainActivity) {

        var tempList = mutableListOf<StorageListResult>()

        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.getStorageList("${tokenManager.getUid()}")?.enqueue(object :
            Callback<StorageListResponse> {
            override fun onResponse(call: Call<StorageListResponse>, response: Response<StorageListResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: StorageListResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    for(i in 0 until result?.result!!.size) {
                        var scriptId = result?.result!!.get(i).scriptId
                        var script = ""
                        if(result?.result!!.get(i).script != null) {
                            script = result?.result!!.get(i).script.toString()
                        } else {
                            script = ""
                        }
                        var gptId = ""
                        if(result?.result!!.get(i).gptId != null) {
                            gptId = result?.result!!.get(i).gptId.toString()
                        } else {
                            gptId = ""
                        }
                        var uid = result?.result!!.get(i).uid
                        var title = result?.result!!.get(i).title
                        var secTime = result?.result!!.get(i).secTime
                        var filePath = result?.result!!.get(i).voiceFilePath

                        var s1 = StorageListResult(scriptId, script, gptId, uid, title, secTime, filePath)
                        tempList.add(s1)
                    }

                    storageListForBottomSheet.value = tempList

                    val bottomSheetFragment = BottomSheetFragment()
                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: StorageListResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<StorageListResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    fun getStorageDetailForBottomSheet(mainActivity: MainActivity, scriptId: Int) {

        var apiClient = ApiClient(mainActivity)
        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.getStorageDetail("${tokenManager.getUid()}", scriptId)?.enqueue(object :
            Callback<StorageDetailResponse> {
            override fun onResponse(call: Call<StorageDetailResponse>, response: Response<StorageDetailResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: StorageDetailResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    storageDetailForBottomSheet.value = StorageDetailResult(result?.result!!.scriptId, result?.result!!.script,  result?.result!!.gptId,  result?.result!!.uid,  result?.result!!.title,  result?.result!!.secTime)

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: StorageDetailResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<StorageDetailResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }
    //ssml 태그를 제외한 일반 스크립트를 반환
    fun replaceScriptToNormal(script: String): String {
        val normalScript = script.replace("\n숨 고르기 (1초)\n", "").replace("\nPPT 넘김 (2초)\n", "")
        return normalScript
    }
    //바텀시트 뷰모델 초기화
    fun clearValueStorageDataForBottomSheet(uid : String) {
        storageDetailForBottomSheet.value = StorageDetailResult(0,"","", uid,"",0)
    }
}