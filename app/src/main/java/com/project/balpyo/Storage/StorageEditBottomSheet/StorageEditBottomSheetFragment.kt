package com.project.balpyo.Storage.StorageEditBottomSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.Storage.StorageUIViewModel
import com.project.balpyo.databinding.FragmentNoteBottomSheetBinding

interface StorageBottomSheetListener {
    fun onNoteSelected(position: Int)
}
class StorageEditBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var listener: StorageBottomSheetListener
    lateinit var binding: FragmentNoteBottomSheetBinding
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as StorageBottomSheetListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBottomSheetBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        binding.run {
            buttonDelete.setOnClickListener {
                onItemClicked(1)
            }

            buttonEdit.setOnClickListener {
                onItemClicked(2)
            }
        }

        return binding.root
    }

    private fun onItemClicked(position: Int) {
        listener.onNoteSelected(position)
        dismiss()
    }
}