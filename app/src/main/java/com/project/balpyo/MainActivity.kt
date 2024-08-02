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
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
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

    var type = ""

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

        MyApplication.mainActivity = true

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

    override fun onResume() {
        super.onResume()
        type = intent?.getStringExtra("type").toString()
        Log.d("발표몇분", "type : ${MyApplication.type}")
        if(MyApplication.type == "push") {
//            navController.navigate(R.id.splashFragment)
//            navController.navigate(R.id.loginFragment)
            navController.navigate(R.id.homeFragment)
            Log.d("발표몇분", "home fragment")
            MyApplication.type = ""
        } else {
            navController.navigate(R.id.splashFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.mainActivity = false
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