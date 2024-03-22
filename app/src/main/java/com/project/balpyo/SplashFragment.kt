package com.project.balpyo

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.project.balpyo.Script.ScriptResultFragment
import com.project.balpyo.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(layoutInflater)

        binding.run {
            Handler().postDelayed({
                val transaction: FragmentTransaction =
                    requireActivity().supportFragmentManager.beginTransaction()
                val LoginFragment = LoginFragment()
                transaction.replace(com.project.balpyo.R.id.fragmentContainerView, LoginFragment)
                transaction.commit()
            }, 1000)
        }

        return binding.root
    }
}