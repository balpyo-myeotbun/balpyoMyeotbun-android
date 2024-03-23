package com.project.balpyo.TimeCalculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ScriptResultFragment
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.api.TokenManager
import com.project.balpyo.databinding.FragmentTimeCalculatorScriptBinding

class TimeCalculatorScriptFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorScriptBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimeCalculatorScriptBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()

        binding.run {
            buttonNext.setOnClickListener {

                MyApplication.timeCalculatorScript = editTextScript.text.toString()

                findNavController().navigate(R.id.timeCalculatorTimeFragment)
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
                text = "2/4"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }
}