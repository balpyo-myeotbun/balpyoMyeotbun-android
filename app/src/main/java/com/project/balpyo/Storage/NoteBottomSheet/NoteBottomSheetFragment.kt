package com.project.balpyo.Storage.NoteBottomSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.Storage.StorageUIViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentFilterBottomSheetBinding
import com.project.balpyo.databinding.FragmentNoteBottomSheetBinding

interface NoteBottomSheetListener {
    fun onNoteSelected(position: Int)
}
class NoteBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var listener: NoteBottomSheetListener
    private lateinit var uiViewModel: StorageUIViewModel
    lateinit var binding: FragmentNoteBottomSheetBinding
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as NoteBottomSheetListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBottomSheetBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        uiViewModel = ViewModelProvider(mainActivity)[StorageUIViewModel::class.java]

        binding.run {
            buttonDelete.setOnClickListener {
                onItemClicked(3)
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