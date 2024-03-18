package com.project.balpyo.FlowController

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentFlowControllerResultBinding

class FlowControllerResultFragment : Fragment() {
    lateinit var binding : FragmentFlowControllerResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFlowControllerResultBinding.inflate(layoutInflater)
        initToolBar()
        return binding.root
    }
    fun initToolBar() {
        binding.run {
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.buttonBack.visibility = View.INVISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonClose.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
        }
    }
}