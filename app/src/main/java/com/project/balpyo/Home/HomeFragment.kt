package com.project.balpyo.Home

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Storage.Adapter.StorageAdapter
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.databinding.FragmentHomeBinding
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonSizeSpec

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: StorageViewModel
    private lateinit var flowControllerViewModel: FlowControllerViewModel

    private lateinit var animationDrawable: AnimationDrawable
    private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivity = activity as MainActivity

        binding = FragmentHomeBinding.inflate(layoutInflater)
        animationDrawable = binding.imageViewLoading.drawable as AnimationDrawable
        mainActivity.binding.bottomNavigation.menu.findItem(R.id.homeFragment).setChecked(true)

        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        viewModel.getStorageList(mainActivity)

        mainActivity.setTransparentStatusBar()

        animationDrawable = binding.imageViewLoading.drawable as AnimationDrawable

        val balloon = Balloon.Builder(mainActivity)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("대본 생성 기능은 한 번에 하나의 대본을 생성할 수 있습니다.")
            .setTextColorResource(R.color.text)
            .setTextSize(12f)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setArrowColorResource(R.color.tooltip_color)
            .setElevation(0)
            .setPaddingLeft(8)
            .setPaddingRight(8)
            .setMarginTop(3)
            .setMarginHorizontal(10)
            .setCornerRadius(2f)
            .setBackgroundDrawableResource(R.drawable.background_tooltip)
//            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .build()

        viewModel.run {
            storageList.observe(mainActivity) {
                binding.run {
                    rvHome.run {
                        //현재 전체 리스트 받아오기 때문에 5개만 보여주기 위함, 추후 api 나눠지면 다시 처리
                        var list = it
                        if (it.size >= 5)
                            list = it.slice(0..4).toMutableList()
                        val storageAdapter = StorageAdapter(list)

                        adapter = storageAdapter

                        layoutManager = LinearLayoutManager(mainActivity)

                        storageAdapter.itemClickListener =
                            object : StorageAdapter.OnItemClickListener {
                                override fun onItemClick(position: Int) {
                                    viewModel.getStorageDetail(
                                        mainActivity,
                                        list[position].id ?: -1
                                    )
                                    val tags = list[position].tags

                                    if (tags != null) {
                                        if (
                                            tags.contains("SCRIPT") ||
                                            tags.contains("FLOW") ||
                                            tags.contains("TIME")
                                        ) {
                                            if (tags.contains("SCRIPT")) {
                                                //TODO: findNavController().navigate()

                                            } else if (tags.contains("FLOW")) {
                                                flowControllerViewModel.initialize()
                                                flowControllerViewModel.setFlowControllerResult(list[position])
                                                val action =
                                                    HomeFragmentDirections.actionHomeFragmentToFlowControllerResultFragment(
                                                        type = "Home"
                                                    )
                                                findNavController().navigate(action)

                                            } else if (tags.contains("TIME")) {
                                                //TODO: findNavController().navigate()
                                            }
                                        } else if (tags.contains("NOTE")) {
                                            //TODO: note 결과 프래그먼트로 이동
                                            findNavController().navigate(R.id.noteFragment)
                                        } else {
                                            //TODO: 예외
                                        }
                                    }
                                    //findNavController().navigate(R.id.storageEditDeleteFragment)
                                }
                            }
                    }
                }
            }
        }

        binding.run {

            ibHomeStorage.setOnClickListener {
                mainActivity.binding.bottomNavigation.selectedItemId = R.id.storageFragment
            }
            btnHomeStorage.setOnClickListener {
                mainActivity.binding.bottomNavigation.selectedItemId = R.id.storageFragment
            }
            llHomeScript.setOnClickListener {
                if(MyApplication.scriptGenerating) {
                    balloon.showAlignBottom(binding.tooltip)
                } else {
                    findNavController().navigate(R.id.scriptTitleFragment)
                }
            }
            llHomeTime.setOnClickListener {
                findNavController().navigate(R.id.timeCalculatorTitleFragment)
            }
            llHomeFlow.setOnClickListener {
                findNavController().navigate(R.id.flowControllerTitleFragment)
            }

            ivBannerHome.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://balpyo.notion.site/BALPYO-2affad5c2bf740bc80ec32e7df45dc5d"))
                startActivity(intent)
            }
            //추후 닉네임 하이라이팅 처리
            val nickName = PreferenceHelper.getUserNickname(mainActivity)
            if (nickName != null) {
                Log.d("", nickName)
                tvHomeStorageTitle.text = "${nickName}님의 보관함"
                val spannableTitle = SpannableString(tvHomeStorageTitle.text)
                spannableTitle.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.primary)),
                    0,
                    nickName.length,
                    SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                )
                tvHomeStorageTitle.text = spannableTitle
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // 대본 생성 로딩 이미지
        binding.run {
            if (MyApplication.scriptGenerating) {
                animationDrawable.start()
                imageViewLoading.visibility = View.VISIBLE
                imageViewBackgroundLoading.visibility = View.VISIBLE
                tooltip.visibility = View.VISIBLE
            } else {
                imageViewLoading.visibility = View.INVISIBLE
                imageViewBackgroundLoading.visibility = View.INVISIBLE
                tooltip.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mainActivity.resetStatusBar()
    }

    //기기의 뒤로가기 버튼을 누를 시
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

}