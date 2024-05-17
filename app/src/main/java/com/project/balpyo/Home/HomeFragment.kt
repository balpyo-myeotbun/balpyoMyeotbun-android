package com.project.balpyo.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.FlowControllerTitleFragment
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ScriptTimeFragment
import com.project.balpyo.Script.ScriptTitleFragment
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.TimeCalculator.TimeCalculatorTitleFragment
import com.project.balpyo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: StorageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]

        binding.run {
            imageViewStorage.setOnClickListener {
                viewModel.getStorageList(this@HomeFragment, mainActivity)
//                findNavController().navigate(R.id.storageFragment)
            }
            menuGenerateScript.setOnClickListener {
                findNavController().navigate(R.id.scriptTitleFragment)
                /*mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ScriptTitleFragment())
                    .addToBackStack(null)
                    .commit()*/
            }
            menuTimeCalculator.setOnClickListener {
                findNavController().navigate(R.id.timeCalculatorTitleFragment)
                /*mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, TimeCalculatorTitleFragment())
                    .addToBackStack(null)
                    .commit()*/
            }
            menuFlowController.setOnClickListener {
                findNavController().navigate(R.id.flowControllerTitleFragment)
                /*mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, FlowControllerTitleFragment())
                    .addToBackStack(null)
                    .commit()*/
            }
        }

        return binding.root
    }
}