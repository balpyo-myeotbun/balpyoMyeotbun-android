package com.project.balpyo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.project.balpyo.databinding.FragmentLoadingBinding
import kotlin.concurrent.timer

class LoadingFragment : Fragment() {

    lateinit var binding: FragmentLoadingBinding
    private val args: LoadingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoadingBinding.inflate(layoutInflater)
        val toolbarTitle = args.toolbarTitle
        val comment = args.comment

        binding.run {
            Glide.with(requireContext()).load(R.raw.loading).into(imageViewLoading)
            toolbar.textViewTitle.text = toolbarTitle
            textViewSubLoading.setText(comment)
        }

        initToolBar()

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.INVISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
        }
    }
}