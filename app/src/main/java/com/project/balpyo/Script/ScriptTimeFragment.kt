package com.project.balpyo.Script

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.project.balpyo.LoadingFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptTimeBinding

class ScriptTimeFragment : Fragment() {

    lateinit var binding: FragmentScriptTimeBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: GenerateScriptViewModel

    var noSuchTime = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptTimeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[GenerateScriptViewModel::class.java]

        initToolBar()

        binding.run {
            spinnerMinute.run {
                wrapSelectorWheel = false
                minValue = 0
                maxValue = 59
            }
            spinnerMinute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            spinnerSecond.run {
                wrapSelectorWheel = false
                minValue = 0
                maxValue = 59
            }
            spinnerSecond.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            imageViewCheckbox.setOnClickListener {
                if(noSuchTime) {
                    imageViewCheckbox.setImageResource(R.drawable.ic_checkbox_unselected)
                    noSuchTime = false
                } else {
                    imageViewCheckbox.setImageResource(R.drawable.ic_checkbox_selected)
                    noSuchTime = true
                }
            }

            buttonNext.setOnClickListener {
                if(noSuchTime) {
                    MyApplication.scrpitTime = 120
                } else {
                    Log.d("발표몇분", "${spinnerMinute.value}")
                    Log.d("발표몇분", "${spinnerSecond.value}")
                    var selectedTime = spinnerMinute.value*60 + spinnerSecond.value
                    MyApplication.scrpitTime = selectedTime.toLong()
                }

                viewModel.generateScript(mainActivity)


                val transaction: FragmentTransaction =
                    requireActivity().supportFragmentManager.beginTransaction()
                val LoadingFragment = LoadingFragment()
                transaction.replace(com.project.balpyo.R.id.fragmentContainerView, LoadingFragment)
                transaction.commit()
            }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "3/3"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
        }
    }
}