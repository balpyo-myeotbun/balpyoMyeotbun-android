package com.project.balpyo.FlowController

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerAddTimeBinding

class FlowControllerAddTimeFragment : Fragment() {
    lateinit var binding: FragmentFlowControllerAddTimeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFlowControllerAddTimeBinding.inflate(layoutInflater)
        initToolBar()
        binding.FCATNextBtn.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            val FlowControllerPreviewFragment = FlowControllerPreviewFragment()
            transaction.replace(com.project.balpyo.R.id.fragmentContainerView, FlowControllerPreviewFragment)
            transaction.commit() }
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.INVISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
            toolbar.buttonClose.setOnClickListener {
                // 닫기 버튼 클릭시 동작
            }
        }
    }
}