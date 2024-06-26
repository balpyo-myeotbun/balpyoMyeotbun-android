package com.project.balpyo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.project.balpyo.Script.Data.ScriptResultData
import com.project.balpyo.Script.ScriptResultFragment
import com.project.balpyo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                    "DetailFragment" -> navController.navigate(R.id.scriptResultFragment)
                    else -> navController.navigate(R.id.homeFragment)
                }
            }
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
}