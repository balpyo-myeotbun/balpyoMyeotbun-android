package com.project.balpyo.Script

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentScriptResultBinding

class ScriptResultFragment : Fragment() {

    lateinit var binding: FragmentScriptResultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptResultBinding.inflate(layoutInflater)

        initToolBar()

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
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