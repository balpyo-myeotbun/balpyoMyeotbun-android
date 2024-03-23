package com.project.balpyo.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentStorageEditDeleteBinding

class StorageEditDeleteFragment : Fragment() {
    lateinit var binding: FragmentStorageEditDeleteBinding
    var editable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStorageEditDeleteBinding.inflate(layoutInflater)
        initToolBar()
        var keyListner = binding.editTextScript.keyListener
        binding.editTextScript.keyListener = null
        binding.run {
            buttonEdit.setOnClickListener {
                if(editable) {
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary)
                    buttonEdit.text = "대본 수정하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    editTextScript.keyListener = null
                } else {
                    buttonEdit.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray4)
                    buttonEdit.text = "대본 저장하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    editTextScript.keyListener = keyListner
                }
                editable = !editable
            }

            buttonDelete.setOnClickListener {

            }
        }
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

}