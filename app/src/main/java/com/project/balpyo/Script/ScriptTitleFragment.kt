package com.project.balpyo.Script

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.FlowControllerResultFragment
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptTitleBinding

class ScriptTitleFragment : Fragment() {

    lateinit var binding: FragmentScriptTitleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptTitleBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {
            buttonNext.setOnClickListener {
                MyApplication.scriptTitle = editTextTitle.text.toString()
                findNavController().navigate(R.id.scriptTopicFragment)
            }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.text = "대본 생성"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "1/5"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }
}