package com.project.balpyo

import android.content.ClipData
import android.content.ClipDescription
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.balpyo.FlowController.AddTime.CustomDragShadowBuilder
import com.project.balpyo.FlowController.AddTime.MultiTypeAdapter
import com.project.balpyo.FlowController.AddTime.data.MultiTypeItem
import com.project.balpyo.FlowController.FlowControllerPreviewFragmentDirections
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.databinding.FragmentStorageEditFlowControllerTimeBinding

class StorageEditFlowControllerTimeFragment : Fragment() {
    private lateinit var binding: FragmentStorageEditFlowControllerTimeBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel
    private val items = mutableListOf<MultiTypeItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding = FragmentStorageEditFlowControllerTimeBinding.inflate(inflater, container, false)
        initToolBar()

        initToolBar()
        initMultiTypeRecyclerView()

        setupLongClickListener(binding.btnFlowControllerBreathStorageFlowControllerEditTime, R.layout.layout_custom_shadow_breath)
        setupLongClickListener(binding.btnFlowControllerPptStorageFlowControllerEditTime, R.layout.layout_custom_shadow_ppt)
        setupDragListener()

        // '다음' 버튼 클릭 리스너 설정
        binding.btnFlowControllerNextStorageFlowControllerEditTime.setOnClickListener {
            val items = (binding.rvScriptStorageFlowControllerEditTime.adapter as MultiTypeAdapter).getList()
            flowControllerViewModel.setCustomScript(concatenateTexts(items))
            //api 호출 하면서 하단 액션 추가
            /*val action = StorageEditFlowControllerTimeFragmentDirections.actionStorageEditFlowControllerTimeFragmentToLoadingFragment(
                toolbarTitle = "발표연습",
                comment = "발표 연습이 만들어지고 있어요"
            )
            findNavController().navigate(action)*/
        }
        return binding.root
    }

    private fun setupDragListeners() {
        val buttons = listOf(binding.btnFlowControllerBreathStorageFlowControllerEditTime, binding.btnFlowControllerPptStorageFlowControllerEditTime)
        buttons.forEach { button ->
            button.setOnLongClickListener { view ->
                val item = ClipData.Item(view.tag as? CharSequence)
                val dragData = ClipData(view.tag.toString(), arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
                val shadowBuilder = View.DragShadowBuilder(view)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.startDragAndDrop(dragData, shadowBuilder, view, 0)
                } else {
                    view.startDrag(dragData, shadowBuilder, view, 0)
                }
                true
            }
        }
    }
    private fun initMultiTypeRecyclerView() {
        // 초기 데이터 가져오기
        val scriptData = flowControllerViewModel.getSplitScriptToSentencesData().value ?: listOf()
        items.clear()
        items.addAll(scriptData.map { MultiTypeItem.TextItem(it) })

        // RecyclerView 및 어댑터 설정
        val recyclerView = binding.rvScriptStorageFlowControllerEditTime
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val multiTypeRecyclerViewAdapter = MultiTypeAdapter(items, binding.rvScriptStorageFlowControllerEditTime)
        recyclerView.adapter = multiTypeRecyclerViewAdapter
    }

    private fun setupLongClickListener(button: Button, dragShadowLayout: Int) {
        button.setOnLongClickListener { view ->
            val item = ClipData.Item(view.tag as? CharSequence)
            val dragData =
                ClipData(view.tag.toString(), arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            val dragShadow = CustomDragShadowBuilder(requireContext(), dragShadowLayout) // 원하는 레이아웃 ID
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.startDragAndDrop(dragData, dragShadow, view, 0)
            } else {
                view.startDrag(dragData, dragShadow, view, 0)
            }
            true
        }
    }

    //자동 스크롤
    private fun setupDragListener() {
        val handler = Handler(Looper.getMainLooper())
        var isScrolling = false

        val scrollRunnableTop = object : Runnable {
            override fun run() {
                if (isScrolling) {
                    binding.rvScriptStorageFlowControllerEditTime.scrollBy(0, -30)
                    handler.postDelayed(this, 50)
                }
            }
        }

        val scrollRunnableBottom = object : Runnable {
            override fun run() {
                if (isScrolling) {
                    binding.rvScriptStorageFlowControllerEditTime.scrollBy(0, 30)
                    handler.postDelayed(this, 50)
                }
            }
        }

        binding.spFlowControllerTop.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_ENTERED, DragEvent.ACTION_DRAG_LOCATION -> {
                    val y = event.y
                    val threshold = 100

                    if (y < threshold && !isScrolling) {
                        isScrolling = true
                        handler.post(scrollRunnableTop)
                    }
                    true
                }
                DragEvent.ACTION_DRAG_EXITED, DragEvent.ACTION_DROP, DragEvent.ACTION_DRAG_ENDED -> {
                    isScrolling = false
                    handler.removeCallbacks(scrollRunnableTop)
                    true
                }
                else -> true
            }
        }

        binding.spFlowControllerBottom.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_ENTERED, DragEvent.ACTION_DRAG_LOCATION -> {
                    val y = event.y
                    val threshold = 100

                    if (y < threshold && !isScrolling) {
                        isScrolling = true
                        handler.post(scrollRunnableBottom)
                    }
                    true
                }
                DragEvent.ACTION_DRAG_EXITED, DragEvent.ACTION_DROP, DragEvent.ACTION_DRAG_ENDED -> {
                    isScrolling = false
                    handler.removeCallbacks(scrollRunnableBottom)
                    true
                }
                else -> true
            }
        }
    }

    private fun concatenateTexts(items: List<MultiTypeItem>): String {
        return items.joinToString("\n") {
            when (it) {
                is MultiTypeItem.TextItem -> it.text
                is MultiTypeItem.BreathButtonItem -> "숨 고르기+1"
                is MultiTypeItem.PPTButtonItem -> "PPT 넘김+2"
            }
        }
    }

    private fun initToolBar() {
        binding.toolbar.run {
            buttonBack.visibility = View.VISIBLE
            buttonClose.visibility = View.INVISIBLE
            textViewTitle.visibility = View.VISIBLE
            textViewTitle.text = "발표 연습"
            textViewPage.visibility = View.VISIBLE
            textViewPage.text = "2/2"
            buttonBack.setOnClickListener { findNavController().popBackStack() }
        }
    }
}
