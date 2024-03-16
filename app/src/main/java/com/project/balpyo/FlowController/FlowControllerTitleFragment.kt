package com.project.balpyo.FlowController

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.project.balpyo.databinding.FragmentFlowControllerTitleBinding


class FlowControllerTitleFragment : Fragment() {
    lateinit var binding: FragmentFlowControllerTitleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentFlowControllerTitleBinding.inflate(layoutInflater)
        initToolBar()
        binding.FlowControllerNextBtn.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            val FlowControllerEditScriptFragment = FlowControllerEditScriptFragment()
            transaction.replace(com.project.balpyo.R.id.fragmentContainerView, FlowControllerEditScriptFragment)
            transaction.commit() }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "1/4"
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
        }
    }
}