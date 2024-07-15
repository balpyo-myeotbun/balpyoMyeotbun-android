package com.project.balpyo.BottomSheet

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.balpyo.BottomSheetAdapter.BottomSheetAdapter
import com.project.balpyo.BottomSheetData.BottomSheetItem
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.databinding.FragmentBottomsheetBinding

interface BottomSheetListener {
    fun onItemClicked(position: Int)
}

class BottomSheetFragment : BottomSheetDialogFragment(), BottomSheetListener {
    lateinit var binding: FragmentBottomsheetBinding
    lateinit var mainActivity: MainActivity
    var scriptId = mutableListOf<Long>()
    var position: Int? = null
    lateinit var viewModel: StorageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBottomsheetBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]

        // 데이터 관찰 및 설정
        viewModel.run {
            storageListForBottomSheet.observe(mainActivity) {
                val items = mutableListOf<BottomSheetItem>()

                for (i in it.indices) {
                    items.add(BottomSheetItem(it[i].title))
                    scriptId.add(it[i].scriptId)
                }

                val adapter = BottomSheetAdapter(items, scriptId, viewModel, mainActivity, this@BottomSheetFragment)
                binding.rvBsScript.adapter = adapter
                binding.rvBsScript.layoutManager = LinearLayoutManager(mainActivity)

                binding.btnBsWrite.setOnClickListener {
                    dismiss()
                }
                binding.btnBsLoad.setOnClickListener {
                    viewModel.getStorageDetailForBottomSheet(mainActivity, scriptId[position!!].toInt())
                    dismiss()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheetBehavior(view)
    }

    override fun onItemClicked(position: Int) {
        if (position != -1) {
            binding.btnBsLoad.isEnabled = true
            this.position = position
        }
    }

    private fun setupBottomSheetBehavior(view: View) {
        val bottomSheet = view.parent as View
        val behavior = BottomSheetBehavior.from(bottomSheet)

        // 화면의 절반 크기
        val halfScreenHeight = resources.displayMetrics.heightPixels / 2
        behavior.peekHeight = halfScreenHeight

        // 드래그 이벤트 리스너
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // 상태 변경 시
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 중인 경우 레이아웃 높이 조정
                val layoutParams = bottomSheet.layoutParams
                val newHeight = halfScreenHeight + (slideOffset * halfScreenHeight).toInt()
                layoutParams.height = newHeight
                bottomSheet.layoutParams = layoutParams

                // 리사이클러뷰 높이 조정 (조정 안하면 리사이클러 뷰에 의해 하단 버튼이 잘림) (핸들, 하단 버튼을 제외하고 남는 공간 만큼)
                val recyclerViewParams = binding.rvBsScript.layoutParams
                recyclerViewParams.height = newHeight - calculateRemainingSpace()
                binding.rvBsScript.layoutParams = recyclerViewParams
            }
        })

        // 바텀시트 초기 상태 설정 (화면의 절반)
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        behavior.isFitToContents = false // 크기 고정
        behavior.skipCollapsed = true // 축소 상태 x

        // 초기 리사이클러뷰 높이 설정 (화면 절반 크기에서 핸들, 하단 버튼을 제외하고 남는 공간 만큼 차지)
        val recyclerViewParams = binding.rvBsScript.layoutParams
        recyclerViewParams.height = halfScreenHeight - calculateRemainingSpace()
        binding.rvBsScript.layoutParams = recyclerViewParams
    }

    // dp를 px로 변환
    private fun Int.dpToPx(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        )
    }

    //핸들, 하단 버튼 제외 남은 공간 계산
    private fun calculateRemainingSpace(): Int {
        binding.btnBsLoad.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        binding.btnBsWrite.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        binding.viewBsHandle.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val buttonHeight = binding.btnBsLoad.measuredHeight + binding.btnBsLoad.marginBottom
        val writeButtonHeight = binding.btnBsWrite.measuredHeight + binding.btnBsWrite.marginBottom
        val handleHeight = binding.viewBsHandle.measuredHeight + binding.viewBsHandle.marginTop + binding.viewBsHandle.marginBottom

        return buttonHeight + writeButtonHeight + handleHeight + binding.rvBsScript.marginBottom + binding.bottomsheet.paddingTop + binding.bottomsheet.paddingBottom
    }
}