package com.project.balpyo.MyPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentMyPageChangePasswordCompleteBinding

class MyPageChangePasswordCompleteFragment : Fragment() {

    lateinit var binding: FragmentMyPageChangePasswordCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyPageChangePasswordCompleteBinding.inflate(layoutInflater)

        binding.run {
            
        }

        return binding.root
    }
}