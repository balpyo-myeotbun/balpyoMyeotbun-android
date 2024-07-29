package com.project.balpyo.Home

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Home.Adapter.StorageAdapter
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
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

                    rvStorage.run {

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
                    //추후 닉네임 하이라이팅 처리
                    val nickName = "발표"
                    val spannableTitle = SpannableString(tvStorageMainNickname.text)
                    spannableTitle.setSpan(
                        ForegroundColorSpan(resources.getColor(R.color.primary)),
                        0,
                        nickName.length,
                        SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    tvStorageMainNickname.text = spannableTitle

                    ivStorageMainBack.setOnClickListener {
                        // 뒤로가기 버튼 클릭시 동작
                        mainActivity.binding.bottomNavigation.selectedItemId = R.id.homeFragment
                    }
                }
            }
        }

        return binding.root
    }
}