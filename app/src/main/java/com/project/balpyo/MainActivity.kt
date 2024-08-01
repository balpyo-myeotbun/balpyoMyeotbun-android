package com.project.balpyo

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.balpyo.Script.Data.ScriptResultData
import com.project.balpyo.Script.ScriptResultFragment
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceUtil
import com.project.balpyo.databinding.ActivityMainBinding
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    lateinit var notificationActivity: NotificationActivity

    lateinit var sharedPreferenceManager: PreferenceUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        notificationActivity = NotificationActivity()

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MyApplication.preferences = PreferenceUtil(applicationContext)

        setFCMToken()

        bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnApplyWindowInsetsListener(null)
        bottomNavigationView.setPadding(0,0,0,0)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        setBottomNavigationView()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.storageFragment, R.id.myPageFragment -> setBottomNavigationVisibility(View.VISIBLE)
                else -> setBottomNavigationVisibility(View.GONE)
            }
        }
    }

    private fun setBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.storageFragment -> {
                    navController.navigate(R.id.storageFragment)
                    true
                }
                R.id.myPageFragment -> {
                    navController.navigate(R.id.myPageFragment)
                    true
                }
                else -> false
            }
        }
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        bottomNavigationView.visibility = visibility
    }

    override fun onResume() {
        super.onResume()
        //비동기 코드, 리림 클릭 시 mainactivity에서 argument를 포함하여 대본 생성 결과로 이동
        if (intent.getBooleanExtra("isNotification", false)) {
            var uid = intent.getStringExtra("uid")
            var title = intent.getStringExtra("title")
            var secTime = intent.getLongExtra("secTime", 0)
            var script = intent.getStringExtra("script")
            var gptId = intent.getStringExtra("gptId")
            var fragment = intent.getStringExtra("fragment")
            var data = ScriptResultData(uid!!, title!!, secTime, script!!, gptId!!)

            val bundle = Bundle()
            bundle.apply {
                this.putSerializable("data", data)
            }

            // 인텐트에서 데이터 가져오기
            val fragmentToLoad = intent.getStringExtra("fragment_to_load")

            var navController = binding.fragmentContainerView.findFragment<NavHostFragment>().navController

            if (fragment != null) {
                when (fragmentToLoad) {
                    "DetailFragment" -> {
                        // 대본 생성 결과 화면으로 이동
                        val notificationIntent = Intent(notificationActivity, NotificationActivity::class.java)
                        startActivity(notificationIntent)
                    }
                    else -> navController.navigate(R.id.homeFragment)
                }
            }
        }
    }

    fun setTransparentStatusBar() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun resetStatusBar() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if (currentFocus is EditText) {
            currentFocus!!.clearFocus()
        }

        return super.dispatchTouchEvent(ev)
    }


    fun setFCMToken(){

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM Token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FCM Token", "$token")
            MyApplication.preferences.setFCMToken(token)
            Log.d("FCM Token", "FCM 토큰 : ${MyApplication.preferences.getFCMToken()}")

            if (this::sharedPreferenceManager.isInitialized) {
                Log.d("FCM Token", "this::sharedPreferenceManager.isInitialized")
                sharedPreferenceManager.setFCMToken(token)
            }
        }
    }
}