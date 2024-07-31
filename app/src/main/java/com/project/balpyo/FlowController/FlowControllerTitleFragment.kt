package com.project.balpyo.FlowController

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.api.TokenManager
import com.project.balpyo.databinding.FragmentFlowControllerTitleBinding
import kr.bydelta.koala.okt.SentenceSplitter


class FlowControllerTitleFragment : Fragment() {
    lateinit var binding: FragmentFlowControllerTitleBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StorageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding=FragmentFlowControllerTitleBinding.inflate(layoutInflater)
        initToolBar()
        binding.btnBottomNext.setOnClickListener {
            flowControllerViewModel.setTitle(binding.etTitle.text.toString())
            findNavController().navigate(R.id.flowControllerEditScriptFragment)
        }
        binding.btnKeyboardNext.setOnClickListener {
            flowControllerViewModel.setTitle(binding.etTitle.text.toString())
            findNavController().navigate(R.id.flowControllerEditScriptFragment)
        }

        binding.btnDelete.setOnClickListener {
            binding.etTitle.text.clear()
            binding.btnBottomNext.isEnabled = false
            binding.btnKeyboardNext.isEnabled = false
        }

        binding.etTitle.addTextChangedListener {
            if(binding.etTitle.text.isNotEmpty()){
                binding.btnBottomNext.isEnabled = true
                binding.btnKeyboardNext.isEnabled = true
                binding.btnDelete.visibility = View.VISIBLE
            }
            else{
                binding.btnBottomNext.isEnabled = false
                binding.btnKeyboardNext.isEnabled = false
                binding.btnDelete.visibility = View.GONE
            }
        }

        observeKeyboardState()
        return binding.root
    }
    private fun observeKeyboardState() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            var originHeight = -1
            if ( binding.root.height > originHeight) {
                originHeight =  binding.root.height
            }

            val visibleFrameSize = Rect()
            binding.root.getWindowVisibleDisplayFrame(visibleFrameSize)

            val visibleFrameHeight = visibleFrameSize.bottom - visibleFrameSize.top
            val keyboardHeight = originHeight - visibleFrameHeight

            if (keyboardHeight > visibleFrameHeight * 0.15) {
                // 키보드가 올라옴
                binding.btnKeyboardNext.visibility = View.VISIBLE
                binding.btnBottomNext.visibility = View.GONE
                binding.btnKeyboardNext.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.btnKeyboardNext.visibility = View.GONE
                binding.btnBottomNext.visibility = View.VISIBLE
            }
        }
    }
    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "발표 연습"
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "1/5"
            toolbar.buttonBack.setOnClickListener {
                viewModel.clearValueStorageDataForBottomSheet(TokenManager(mainActivity).getUid()!!)
                flowControllerViewModel.initialize()
                findNavController().popBackStack()
            }
        }
    }
}