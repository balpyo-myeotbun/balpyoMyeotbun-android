package com.project.balpyo.MyPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.PreferenceHelper
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

        initToolBar()
        binding.run {

            buttonAccountCancel.setOnClickListener {
                findNavController().navigate(R.id.myPageCancelAccountFragment)
            }

            buttonChangePassword.setOnClickListener {

            }

            buttonEdit.setOnClickListener {

            }

            textViewUserEmail.text = PreferenceHelper.getUserId(mainActivity)
            textViewUserEmail.text = PreferenceHelper.getUserNickname(mainActivity)
        }
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "내 정보 관리"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}