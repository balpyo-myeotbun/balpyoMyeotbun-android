package com.project.balpyo.FlowController

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.project.balpyo.databinding.FragmentFlowControllerEditScriptBinding

class FlowControllerEditScriptFragment : Fragment() {
    lateinit var binding: FragmentFlowControllerEditScriptBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFlowControllerEditScriptBinding.inflate(layoutInflater)
        initToolBar()
        binding.FCEDNextBtn.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            val FlowControllerAddTimeFragment = FlowControllerAddTimeFragment()
            transaction.replace(com.project.balpyo.R.id.fragmentContainerView, FlowControllerAddTimeFragment)
            transaction.commit() }
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "2/4"
            toolbar.buttonBack.setOnClickListener {
                // Handle back button click
            }
        }
    }
}
