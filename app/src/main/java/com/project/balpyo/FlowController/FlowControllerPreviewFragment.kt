package com.project.balpyo.FlowController

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.project.balpyo.databinding.FragmentFlowControllerPreviewBinding

class FlowControllerPreviewFragment : Fragment() {
    lateinit var binding:FragmentFlowControllerPreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFlowControllerPreviewBinding.inflate(layoutInflater)
        initToolBar()
        binding.FCPNextBtn.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            val FlowControllerResultFragment = FlowControllerResultFragment()
            transaction.replace(com.project.balpyo.R.id.fragmentContainerView, FlowControllerResultFragment)
            transaction.commit()
        }
        return binding.root
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "4/4"
            toolbar.buttonBack.setOnClickListener {
                // Handle back button click
            }
        }
    }
}