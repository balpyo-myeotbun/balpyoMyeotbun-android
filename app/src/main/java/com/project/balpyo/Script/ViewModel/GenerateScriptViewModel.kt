package com.project.balpyo.Script.ViewModel

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.GenerateScriptRequest
import com.project.balpyo.api.response.GenerateScriptResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerateScriptViewModel : ViewModel() {
    var script = MutableLiveData<String>()
    var gptId = MutableLiveData<String>()
    fun showNotification(context : Context, title: String, secTime : Long, uid : String, script : String, gptId : String ) {

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("isNotification", true)
        intent.putExtra("title", title)
        intent.putExtra("secTime", secTime)
        intent.putExtra("uid", uid)
        intent.putExtra("script", script)
        intent.putExtra("gptId", gptId)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // 알림 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID",
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel Description"
            }

            // 알림 매니저에 채널 추가
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        // 알림을 만들고 설정
        val notification = NotificationCompat.Builder(context, "CHANNEL_ID")
            .setContentTitle("대본이 완성되었어요!")
            .setContentText("알림을 클릭하여 대본을 확인해보세요.")
            .setSmallIcon(R.drawable.ic_logo_balpyo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        //알림을 표시
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한 요청 다이얼로그를 표시
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
                return
            }
            notify(0, notification)
        }
    }
    fun generateScript(fragment: Fragment, mainActivity: MainActivity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var apiClient = ApiClient(mainActivity)
                var tokenManager = TokenManager(mainActivity)

                var inputScriptInfo = GenerateScriptRequest(
                    MyApplication.scriptSubject,
                    MyApplication.scriptSubtopic,
                    MyApplication.scrpitTime,
                    "1234",
                    "false"
                )

                Log.d("##", "scripit info : ${inputScriptInfo}")

                apiClient.apiService.generateScript("${tokenManager.getUid()}", inputScriptInfo)
                    ?.enqueue(object :
                        Callback<GenerateScriptResponse> {
                        override fun onResponse(
                            call: Call<GenerateScriptResponse>,
                            response: Response<GenerateScriptResponse>
                        ) {
                            if (response.isSuccessful) {
                                // 정상적으로 통신이 성공된 경우
                                var result: GenerateScriptResponse? = response.body()
                                Log.d("##", "onResponse 성공: " + result?.toString())
                                script.value = result?.result!!.resultScript.get(0).message.content
                                gptId.value = result?.result!!.gptId
                                MyApplication.scriptTitle = MyApplication.scriptTitle
                                MyApplication.scrpitTime = MyApplication.scrpitTime
                                var viewModel = ViewModelProvider(mainActivity)[GenerateScriptViewModel::class.java]
                                findNavController(fragment).navigate(R.id.scriptResultFragment)
                                //showNotification(mainActivity, MyApplication.scriptTitle, MyApplication.scrpitTime, TokenManager(mainActivity).getUid()!!, result?.result!!.resultScript.get(0).message.content, viewModel.gptId.value!! )
                            } else {
                                // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                var result: GenerateScriptResponse? = response.body()
                                Log.d("##", "onResponse 실패")
                                Log.d("##", "onResponse 실패: " + response.code())
                                Log.d("##", "onResponse 실패: " + response.body())
                                val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                                Log.d("##", "Error Response: $errorBody")

                                findNavController(fragment).navigate(R.id.scriptTimeFragment)
                            }
                        }

                        override fun onFailure(call: Call<GenerateScriptResponse>, t: Throwable) {
                            // 통신 실패
                            Log.d("##", "onFailure 에러: " + t.message.toString());
                            findNavController(fragment).navigate(R.id.scriptTimeFragment)
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}