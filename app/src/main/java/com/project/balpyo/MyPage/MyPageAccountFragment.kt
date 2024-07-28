package com.project.balpyo.MyPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentMyPageAccountBinding

class MyPageAccountFragment : Fragment() {
    lateinit var binding: FragmentMyPageAccountBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageAccountBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

}