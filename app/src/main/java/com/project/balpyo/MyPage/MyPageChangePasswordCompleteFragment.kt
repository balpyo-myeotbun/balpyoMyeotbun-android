package com.project.balpyo.MyPage

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentMyPageChangePasswordCompleteBinding

class MyPageChangePasswordCompleteFragment : Fragment() {

    lateinit var binding: FragmentMyPageChangePasswordCompleteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyPageChangePasswordCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        Handler().postDelayed({
            findNavController().popBackStack(R.id.myPageAccountFragment, false)
        }, 2000)

        return binding.root
    }
}