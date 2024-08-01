package com.project.balpyo.Onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentOnboarding1Binding

class Onboarding1Fragment : Fragment() {

    lateinit var binding: FragmentOnboarding1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboarding1Binding.inflate(layoutInflater)

        binding.run {
            buttonNext.setOnClickListener {
                findNavController().navigate(R.id.onboarding2Fragment)
            }
        }

        return binding.root
    }
}