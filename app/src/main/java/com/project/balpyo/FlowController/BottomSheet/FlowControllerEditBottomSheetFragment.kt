package com.project.balpyo.FlowController.BottomSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.databinding.FragmentFlowControllerEditBottomSheetBinding

interface FlowControllerEditBottomSheetListener {
    fun onItemSelected(position: Int)
}

class FlowControllerEditBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentFlowControllerEditBottomSheetBinding
    lateinit var mainActivity: MainActivity
    var position: Int? = null
    lateinit var viewModel: StorageViewModel
    private lateinit var listener: FlowControllerEditBottomSheetListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as FlowControllerEditBottomSheetListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFlowControllerEditBottomSheetBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        binding.flFlowEditBsDelete.setOnClickListener {
            onItemClicked(0)
        }
        binding.flFlowEditBsEdit.setOnClickListener {
            onItemClicked(1)
        }

        return binding.root
    }

    private fun onItemClicked(position: Int) {
        listener.onItemSelected(position)
        dismiss()
    }

}