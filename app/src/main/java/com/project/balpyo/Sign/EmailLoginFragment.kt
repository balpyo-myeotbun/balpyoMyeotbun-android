package com.project.balpyo.Sign

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Sign.ViewModel.SignViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentEmailLoginBinding

class EmailLoginFragment : Fragment() {
    lateinit var binding: FragmentEmailLoginBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: SignViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailLoginBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[SignViewModel::class.java]

        binding.run {
            btnEmailLogin.setOnClickListener {
                MyApplication.email = etEmailLoginEmail.text.trim().toString()
                MyApplication.password = etEmailLoginPw.text.trim().toString()
                viewModel.signInForNavigate(this@EmailLoginFragment, mainActivity)
            }
            tvEmailLogin.setOnClickListener {
                findNavController().navigate(R.id.signUpEmailFragment)
            }
        }
        initToolBar()
        return binding.root
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "이메일 로그인"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}