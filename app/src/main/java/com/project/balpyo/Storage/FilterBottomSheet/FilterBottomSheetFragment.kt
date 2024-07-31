package com.project.balpyo.Storage.FilterBottomSheet

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Storage.StorageUIViewModel
import com.project.balpyo.databinding.FragmentFilterBottomSheetBinding

interface FilterBottomSheetListener {
    fun onFilterSelected(position: Int)
}
class FilterBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var listener: FilterBottomSheetListener
    private lateinit var uiViewModel: StorageUIViewModel
    lateinit var binding: FragmentFilterBottomSheetBinding
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as FilterBottomSheetListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        uiViewModel = ViewModelProvider(mainActivity)[StorageUIViewModel::class.java]

        binding.run {
            llFilterBsScript.setOnClickListener { onItemClicked(0) }
            llFilterBsTime.setOnClickListener { onItemClicked(1) }
            llFilterBsFlow.setOnClickListener { onItemClicked(2) }
            llFilterBsNote.setOnClickListener { onItemClicked(3) }
        }

        uiViewModel.selectedFilter.observe(viewLifecycleOwner) { position ->
            updateUI(position)
        }

        return binding.root
    }

    private fun onItemClicked(position: Int) {
        listener.onFilterSelected(position)
        updateUI(position)
        dismiss()
    }

    private fun updateUI(position: Int) {
        binding.run {
            tvFilterBsScript.setTextColor(mainActivity.getColor(R.color.disabled))
            tvFilterBsTime.setTextColor(mainActivity.getColor(R.color.disabled))
            tvFilterBsFlow.setTextColor(mainActivity.getColor(R.color.disabled))
            tvFilterBsNote.setTextColor(mainActivity.getColor(R.color.disabled))
            ivFilterBsScript.visibility = View.INVISIBLE
            ivFilterBsTime.visibility = View.INVISIBLE
            ivFilterBsFlow.visibility = View.INVISIBLE
            ivFilterBsNote.visibility = View.INVISIBLE

            when(position){
                0 -> {
                    tvFilterBsScript.setTextColor(mainActivity.getColor(R.color.black))
                    ivFilterBsScript.visibility = View.VISIBLE
                }
                1 -> {
                    tvFilterBsTime.setTextColor(mainActivity.getColor(R.color.black))
                    ivFilterBsTime.visibility = View.VISIBLE
                }
                2 -> {
                    tvFilterBsFlow.setTextColor(mainActivity.getColor(R.color.black))
                    ivFilterBsFlow.visibility = View.VISIBLE
                }
                3 -> {
                    tvFilterBsNote.setTextColor(mainActivity.getColor(R.color.black))
                    ivFilterBsNote.visibility = View.VISIBLE
                }
            }
        }
    }
}
