package com.project.balpyo.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        binding.run {
            imageViewStorage.setOnClickListener {
                
            }
            menuGenerateScript.setOnClickListener {

            }
            menuTimeCalculator.setOnClickListener {

            }
            menuFlowController.setOnClickListener {

            }
        }

        return binding.root
    }
}