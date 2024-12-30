package com.project.balpyo.Script

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentScriptCompleteBinding

class ScriptCompleteFragment : Fragment() {

    lateinit var binding: FragmentScriptCompleteBinding

    lateinit var mainActivity: MainActivity

    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.POST_NOTIFICATIONS)

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted ->

        if(isGranted) {
            // 요청 승인
            Log.d("발표몇분", "권한 요청 승인")
            findNavController().navigate(R.id.homeFragment)
        } else {
            // 권한 설정 실패
            Log.d("발표몇분", "권한 설정 실패")
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()

        binding.run {

            Glide.with(mainActivity).load(R.raw.script_check).override(560, 560).into(binding.imageViewCheck)

            buttonAlarm.setOnClickListener {
                requestPermission()
            }

            buttonCheck.setOnClickListener {
                findNavController().navigate(R.id.homeFragment)
            }
        }

        return binding.root
    }

    private fun requestPermission(){
        var permissionCheck = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.POST_NOTIFICATIONS)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //설명이 필요한지
            if(ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.POST_NOTIFICATIONS)){
                //설명 필요 (사용자가 요청을 거부한 적이 있음)
                ActivityCompat.requestPermissions(mainActivity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
            } else {
                //설명 필요 X
                ActivityCompat.requestPermissions(mainActivity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
            }
        }else{
            //권한 허용
        }
        findNavController().navigate(R.id.homeFragment)
    }

    fun initToolBar() {
        binding.run {
            toolbar.imageViewButtonBack.visibility = View.VISIBLE
            toolbar.imageViewButtonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.text = "대본 생성"
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.imageViewButtonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
    }
}