package com.project.balpyo.Script

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.LoadingFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptTimeBinding

class ScriptTimeFragment : Fragment() {

    lateinit var binding: FragmentScriptTimeBinding
    lateinit var mainActivity: MainActivity

    var noSuchTime = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptTimeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()

        binding.run {
            spinnerMinute.run {
//                wrapSelectorWheel = false
                minValue = 0
                maxValue = 2
                value = 2
            }
            spinnerMinute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            spinnerSecond.run {
//                wrapSelectorWheel = false
                minValue = 0
                maxValue = 59
                value = 59
            }
            spinnerSecond.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            buttonNoSpecificTime.setOnClickListener {
                if(noSuchTime) {
                    imageViewCheck.setImageResource(R.drawable.ic_check_unselected)
                    buttonNoSpecificTime.setBackgroundResource(R.drawable.background_box_unselected)
                    noSuchTime = false
                } else {
                    imageViewCheck.setImageResource(R.drawable.ic_check_selected)
                    buttonNoSpecificTime.setBackgroundResource(R.drawable.background_box_selected)
                    noSuchTime = true
                }
            }

            buttonNext.setOnClickListener {
                moveFragment()
            }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.text = "대본 생성"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "4/5"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }

    fun moveFragment() {

        binding.run {
            if(noSuchTime) {
                MyApplication.scriptTime = 180
                MyApplication.scriptTimeString = "원하는 발표 시간 없음"
            } else {
                Log.d("발표몇분", "${spinnerMinute.value}")
                Log.d("발표몇분", "${spinnerSecond.value}")
                var selectedTime = spinnerMinute.value*60 + spinnerSecond.value
                MyApplication.scriptTime = selectedTime.toLong()
                if(spinnerMinute.value != 0) {
                    MyApplication.scriptTimeString = "${spinnerMinute.value}분 ${spinnerSecond.value}초"
                } else {
                    MyApplication.scriptTimeString = "${spinnerSecond.value}초"
                }
            }
        }

        findNavController().navigate(R.id.scriptCheckFragment)
    }

    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
    }
}