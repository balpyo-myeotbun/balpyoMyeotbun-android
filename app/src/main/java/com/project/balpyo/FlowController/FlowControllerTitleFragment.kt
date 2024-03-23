package com.project.balpyo.FlowController

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerTitleBinding


class FlowControllerTitleFragment : Fragment() {
    lateinit var binding: FragmentFlowControllerTitleBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding=FragmentFlowControllerTitleBinding.inflate(layoutInflater)
        initToolBar()
        binding.FlowControllerNextBtn.setOnClickListener {
            flowControllerViewModel.setTitle(binding.FlowControllerEditTitle.text.toString())
            findNavController().navigate(R.id.flowControllerEditScriptFragment)}

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "1/5"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}