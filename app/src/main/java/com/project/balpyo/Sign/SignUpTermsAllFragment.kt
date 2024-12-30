package com.project.balpyo.Sign

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.balpyo.LoadingFragmentArgs
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentSignUpTermsAllBinding

class SignUpTermsAllFragment : Fragment() {
    lateinit var binding: FragmentSignUpTermsAllBinding
    private val args: SignUpTermsAllFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpTermsAllBinding.inflate(layoutInflater)
        initToolBar()
        binding.run {
            toolbar.textViewTitle.text = args.title
            tvTermsAllSignUp.text = args.content
        }
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.imageViewButtonBack.visibility = View.VISIBLE
            toolbar.imageViewButtonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.imageViewButtonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

}