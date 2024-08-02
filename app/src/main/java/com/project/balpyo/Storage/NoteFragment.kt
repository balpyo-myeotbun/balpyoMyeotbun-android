package com.project.balpyo.Storage

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Storage.NoteBottomSheet.NoteBottomSheetFragment
import com.project.balpyo.Storage.NoteBottomSheet.NoteBottomSheetListener
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentNoteBinding

class NoteFragment : Fragment(), NoteBottomSheetListener {

    lateinit var binding: FragmentNoteBinding
    lateinit var mainActivity: MainActivity

    var bottomSheet = NoteBottomSheetFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNoteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()

        binding.run {
            editTextTitle.addTextChangedListener {
                checkText()
            }

            editTextNote.addTextChangedListener {
                checkText()
            }

            buttonStore.setOnClickListener {
                Toast.makeText(mainActivity, "저장되었습니다", Toast.LENGTH_SHORT).show()
                binding.buttonStore.visibility = View.GONE
                binding.buttonMenu.visibility = View.VISIBLE
            }

            buttonMenu.setOnClickListener {
                bottomSheet.show(childFragmentManager,bottomSheet.tag)
            }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            binding.buttonStore.visibility = View.VISIBLE
            binding.buttonMenu.visibility = View.GONE

            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    fun checkText() {
        binding.run {
            if(editTextTitle.text.isNotEmpty() && editTextNote.text.isNotEmpty()) {
                buttonStore.setTextColor(R.color.text)
                // 저장하기 기능 구현
            }
            else {
                buttonStore.setTextColor(R.color.disabled)
            }
        }
    }

    override fun onNoteSelected(position: Int) {
        if (position == 2) {
            Toast.makeText(mainActivity, "수정 후 저장하기를 눌러주세요", Toast.LENGTH_SHORT).show()
            binding.buttonStore.visibility = View.VISIBLE
            binding.buttonMenu.visibility = View.GONE
        } else {

        }
    }
}