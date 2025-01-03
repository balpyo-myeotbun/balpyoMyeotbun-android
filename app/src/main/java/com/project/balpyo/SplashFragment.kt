package com.project.balpyo

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceUtil
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSplashBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        mainActivity.setTransparentStatusBar()
        MyApplication.preferences = PreferenceUtil(mainActivity)

        binding.run {
            val token = PreferenceHelper.getUserToken(requireContext())
            if (token != null) {
                // 자동 로그인
                Handler().postDelayed({
                    findNavController().navigate(R.id.homeFragment)
                }, 3000)
            } else {
                Handler().postDelayed({
                    if(MyApplication.preferences.getOnBoarding("isFirst", true)) {
                        findNavController().navigate(R.id.onboarding1Fragment)
                    } else {
                        findNavController().navigate(R.id.loginFragment)
                    }
                }, 3000)
            }
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        mainActivity.resetStatusBar()
    }
}