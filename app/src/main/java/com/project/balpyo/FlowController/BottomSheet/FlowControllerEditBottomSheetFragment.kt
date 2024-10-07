package com.project.balpyo.FlowController.BottomSheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Storage.LoadScriptBottomSheet.Adapter.LoadScriptBottomSheetAdapter
import com.project.balpyo.Storage.LoadScriptBottomSheet.Data.BottomSheetData
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.databinding.FragmentBottomsheetBinding
import com.project.balpyo.databinding.FragmentFlowControllerEditBottomSheetBinding

class FlowControllerEditBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentFlowControllerEditBottomSheetBinding
    lateinit var mainActivity: MainActivity
    var position: Int? = null
    lateinit var viewModel: StorageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFlowControllerEditBottomSheetBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]

        // 데이터 관찰 및 설정
        viewModel.run {
            storageListForBottomSheet.observe(mainActivity) {

                binding.tvFlowEditBsEdit.setOnClickListener {
                    dismiss()
                }
                binding.tvFlowEditBsDelete.setOnClickListener {
                    dismiss()
                }
            }
        }

        return binding.root
    }
    }