package com.project.balpyo.Onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceUtil
import com.project.balpyo.databinding.FragmentOnboarding1Binding
import com.project.balpyo.databinding.FragmentOnboarding5Binding

class Onboarding5Fragment : Fragment() {
    lateinit var binding: FragmentOnboarding5Binding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboarding5Binding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        MyApplication.preferences = PreferenceUtil(mainActivity)

        binding.run {
            buttonStart.setOnClickListener {
                MyApplication.preferences.setOnBoarding("isFirst", false)
                findNavController().navigate(R.id.loginFragment)
            }
        }

        return binding.root
    }
}