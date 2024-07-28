package com.project.balpyo.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Home.Adapter.StorageAdapter
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.Adapter.SubTopicAdapter
import com.project.balpyo.databinding.FragmentStorageBinding

class StorageFragment : Fragment() {

    lateinit var binding: FragmentStorageBinding

    lateinit var mainActivity: MainActivity

    lateinit var viewModel: StorageViewModel

    private lateinit var flowControllerViewModel: FlowControllerViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStorageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        viewModel.run {
            storageList.observe(mainActivity) {
                binding.run {

                    recyclerViewList.run {

                        var storageAdapter = StorageAdapter(it)
                        adapter = storageAdapter

                        layoutManager = LinearLayoutManager(mainActivity)

                        storageAdapter.itemClickListener =
                            object : StorageAdapter.OnItemClickListener {
                                override fun onItemClick(position: Int) {
                                    viewModel.getStorageDetail(this@StorageFragment, mainActivity, it.get(position).scriptId.toInt())
                                    if(viewModel.storageList.value?.get(position)?.voiceFilePath != null) {
                                        flowControllerViewModel.setIsEdit(true)
                                    }
                                    findNavController().navigate(R.id.storageEditDeleteFragment)
                                }
                            }
                    }
                }
            }
        }

        initToolBar()


        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                mainActivity.binding.bottomNavigation.selectedItemId = R.id.homeFragment
            }
        }
    }
}