package com.project.balpyo.Home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.project.balpyo.FlowController.FlowControllerTitleFragment
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Home.Adapter.BannerVPAdapter
import com.project.balpyo.Home.Adapter.StorageAdapter
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ScriptTimeFragment
import com.project.balpyo.Script.ScriptTitleFragment
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.TimeCalculator.TimeCalculatorTitleFragment
import com.project.balpyo.api.ApiClient
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.request.SignInRequest
import com.project.balpyo.api.request.SignUpRequest
import com.project.balpyo.api.response.BaseResponse
import com.project.balpyo.api.response.GenerateUidResponse
import com.project.balpyo.api.response.SignInResponse
import com.project.balpyo.api.response.StorageListResult
import com.project.balpyo.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: StorageViewModel
    private val handler = Handler(Looper.getMainLooper())
    //배너 자동 스크롤
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val currentItem = binding.vpHome.currentItem
            val totalItemCount = binding.vpHome.adapter?.itemCount ?: 0
            val nextItem = if (currentItem + 1 >= totalItemCount) 0 else currentItem + 1
            binding.vpHome.setCurrentItem(nextItem, true)
            handler.postDelayed(this, 3000)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
        mainActivity.setTransparentStatusBar()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        //flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        viewModel.getStorageList(this@HomeFragment, mainActivity)

        viewModel.run {
            storageList.observe(mainActivity) {
                binding.run {

                    rvHome.run {
                        //현재 전체 리스트 받아오기 때문에 5개만 보여주기 위함, 추후 api 나눠지면 다시 처리
                        var list = it
                        if(it.size >= 5)
                            list = it.slice(0..4).toMutableList()
                        val storageAdapter = StorageAdapter(list)

                        adapter = storageAdapter

                        layoutManager = LinearLayoutManager(mainActivity)

                        storageAdapter.itemClickListener =
                            object : StorageAdapter.OnItemClickListener {
                                override fun onItemClick(position: Int) {
                                    viewModel.getStorageDetail(this@HomeFragment, mainActivity, it.get(position).scriptId.toInt())
                                    if(viewModel.storageList.value?.get(position)?.voiceFilePath != null) {
                                       // flowControllerViewModel.setIsEdit(true)
                                    }
                                    findNavController().navigate(R.id.storageEditDeleteFragment)
                                }
                            }
                    }
                }
            }
        }
        setupBannerViewPager()

        binding.run {
            ibHomeStorage.setOnClickListener {
                mainActivity.binding.bottomNavigation.selectedItemId = R.id.storageFragment
            }
            btnHomeStorage.setOnClickListener {
                mainActivity.binding.bottomNavigation.selectedItemId = R.id.storageFragment
            }
            llHomeScript.setOnClickListener {
                findNavController().navigate(R.id.scriptTitleFragment)
            }
            llHomeTime.setOnClickListener {
                findNavController().navigate(R.id.timeCalculatorTitleFragment)
            }
            llHomeFlow.setOnClickListener {
                findNavController().navigate(R.id.flowControllerTitleFragment)
            }

            //추후 닉네임 하이라이팅 처리
            val nickName = "발표"
            val spannableTitle = SpannableString(tvHomeStorageTitle.text)
            spannableTitle.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.primary)),
                0,
                nickName.length,
                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
            )
            tvHomeStorageTitle.text = spannableTitle
        }

        return binding.root
    }

    private fun setupBannerViewPager() {
        val bannerAdapter = BannerVPAdapter(this)

        bannerAdapter.addFragment(BannerFragment(resources.getColor(R.color.tag_flow_bg), "https://balpyo.notion.site/BALPYO-2affad5c2bf740bc80ec32e7df45dc5d"))
        bannerAdapter.addFragment(BannerFragment(resources.getColor(R.color.tag_time_bg), "https://balpyo.notion.site/BALPYO-2affad5c2bf740bc80ec32e7df45dc5d"))
        bannerAdapter.addFragment(BannerFragment(resources.getColor(R.color.tag_script_bg), "https://balpyo.notion.site/BALPYO-2affad5c2bf740bc80ec32e7df45dc5d"))
        binding.vpHome.adapter = bannerAdapter
        binding.vpHome.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpHome.setCurrentItem(0, false) // 초기 페이지 설정
        val spannablePage = SpannableString(binding.tvHomePage.text)
        spannablePage.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.white)),
            0,
            1,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        binding.tvHomePage.text = spannablePage
        binding.vpHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val totalItemCount = binding.vpHome.adapter?.itemCount ?: 0
                binding.tvHomePage.text = "${position + 1}/$totalItemCount"
                val spannablePage = SpannableString(binding.tvHomePage.text)
                spannablePage.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.white)),
                    0,
                    "${position + 1}".length,
                    SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                )
                binding.tvHomePage.text = spannablePage
            }
        })

        handler.post(autoScrollRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoScrollRunnable)
        mainActivity.resetStatusBar()
    }

}