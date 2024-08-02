package com.project.balpyo

import android.R.id
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.project.balpyo.Script.ViewModel.ScriptDetailViewModel
import com.project.balpyo.databinding.ActivityNotificationBinding


class NotificationActivity : AppCompatActivity() {

    var scriptId = 0

    lateinit var binding: ActivityNotificationBinding
    lateinit var scriptViewModel: ScriptDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scriptId = intent?.getStringExtra("scriptId")!!.toInt()
        Log.d("발표몇분", "scriptId in activity : $scriptId")

        scriptViewModel = ViewModelProvider(this)[ScriptDetailViewModel::class.java]

        scriptViewModel.getScriptDetail(this, scriptId)
    }
}