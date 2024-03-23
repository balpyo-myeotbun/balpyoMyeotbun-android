package com.project.balpyo.TimeCalculator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.GenerateAudioRequest
import com.project.balpyo.api.response.TimeCalculatorResponse
import com.project.balpyo.databinding.FragmentTimeCalculatorResultBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class TimeCalculatorResultFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorResultBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTimeCalculatorResultBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
            toolbar.buttonClose.setOnClickListener {
                // 닫기 버튼 클릭시 동작
                val navController = findNavController()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestination, true) // 스택의 처음부터 현재 위치까지 모두 팝
                    .build()

                navController.navigate(R.id.homeFragment, null, navOptions)
            }
        }
    }
}