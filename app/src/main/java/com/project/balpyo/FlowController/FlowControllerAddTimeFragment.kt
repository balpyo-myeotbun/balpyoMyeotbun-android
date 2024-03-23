package com.project.balpyo.FlowController

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.R
import com.project.balpyo.FlowController.data.EditScriptItem
import com.project.balpyo.databinding.FragmentFlowControllerAddTimeBinding

class FlowControllerAddTimeFragment : Fragment() {
    lateinit var binding: FragmentFlowControllerAddTimeBinding
    private lateinit var flowControllerViewModel: FlowControllerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        binding = FragmentFlowControllerAddTimeBinding.inflate(layoutInflater)
        initToolBar()

        val restButton = binding.FCAT2Sec
        restButton.setOnLongClickListener { view ->
            // 드래그 데이터 준비
            val item = ClipData.Item(view.tag as? CharSequence)
            val dragData = ClipData(
            view.tag.toString(),
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            item
        )
            val shadowBuilder = View.DragShadowBuilder(view)

            // 드래그 시작
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(dragData, shadowBuilder, view, 0);
            } else {
                view.startDrag(dragData, shadowBuilder, view, 0);
            }

            true // 이벤트 처리 완료
        }

        // 외부 버튼에 대한 참조를 얻은 후 리스너 설정
        val pptButton = binding.FCAT3Sec
        pptButton.setOnLongClickListener { view ->
            // 드래그 데이터 준비
            val item = ClipData.Item(view.tag as? CharSequence)
            val dragData = ClipData(
                view.tag.toString(),
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )
            val shadowBuilder = View.DragShadowBuilder(view)

            // 드래그 시작
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(dragData, shadowBuilder, view, 0);
            } else {
                view.startDrag(dragData, shadowBuilder, view, 0);
            }

            true // 이벤트 처리 완료
        }


        val items = mutableListOf<EditScriptItem>()
        print(flowControllerViewModel.getSplitScriptToSentencesData().value)
        for(i in 0 until flowControllerViewModel.getSplitScriptToSentencesData().value!!.size){
            items.add(EditScriptItem.TextItem(flowControllerViewModel.getSplitScriptToSentencesData().value!![i]))
        }
        print(items)

        val recyclerView = binding.FCATRV
        val adapter = MultiTypeAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter

        recyclerView.setOnDragListener { view, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DRAG_ENTERED -> true
                DragEvent.ACTION_DRAG_LOCATION -> {
                    // 드래그 위치 계산 및 업데이트
                    val targetPosition = calculateDragPosition(items, recyclerView, dragEvent.x, dragEvent.y)
                    Log.d("",targetPosition.toString())
                    if(targetPosition > 0 && targetPosition < items.size){
                        Log.d("",targetPosition.toString())
                        adapter.showDragPosition(targetPosition)
                    }
                    true
                }
                DragEvent.ACTION_DROP -> {
                    adapter.clearDragPosition()
                    val itemText = dragEvent.clipData.getItemAt(0).text.toString()
                    var position = calculateDragPosition(
                        items,
                        recyclerView,
                        dragEvent.x,
                        dragEvent.y
                    )
                    // 드롭된 위치에 아이템 추가
                    val newItem = EditScriptItem.ButtonItem(itemText, "\n$itemText\n")
                    if (position >= 0 && position <= items.size) {
                        items.add(position, newItem)
                        adapter.notifyItemInserted(position)
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    // 드래그 종료: 임시 divider 제거
                    adapter.clearDragPosition()
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    adapter.clearDragPosition()
                    true
                }
                else -> false
            }
        }

        binding.FCATNextBtn.setOnClickListener {
            flowControllerViewModel.setCustomScript(concatenateTexts(items))
            findNavController().navigate(R.id.flowControllerSpeedFragment)
        }
        return binding.root
    }

    fun calculateDragPosition(
        items: MutableList<EditScriptItem>,
        recyclerView: RecyclerView,
        x: Float,
        y: Float
    ): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        var closestPosition = RecyclerView.NO_POSITION
        var minDistance = Float.MAX_VALUE

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val bounds = Rect()
            child.getHitRect(bounds)

            // 드래그 포인트가 RecyclerView의 상단 근처에 있는 경우
            if (y < recyclerView.getChildAt(0).top) {
                return firstVisiblePosition
            }

            // 드래그 포인트가 RecyclerView의 하단 근처에 있는 경우
            if (y > recyclerView.getChildAt(recyclerView.childCount - 1).bottom) {
                return lastVisiblePosition
            }


            // 드래그 위치가 현재 아이템 위에 있을 때
            if (y >= bounds.top && y <= bounds.bottom) {
                // 아이템의 상단 또는 하단에 더 가까운지 판단하여 처리
                closestPosition = if (y < bounds.centerY()) layoutManager.getPosition(child)
                else layoutManager.getPosition(child) + 1
                break // 올바른 위치를 찾았으므로 반복 종료
            }

            // 드래그 포인트가 아이템의 상단 또는 하단에 위치하는 경우를 고려하여 가장 가까운 위치 찾기
            val distanceTop = Math.abs(bounds.top - y)
            val distanceBottom = Math.abs(bounds.bottom - y)
            val distance = Math.min(distanceTop, distanceBottom)

            if (distance < minDistance) {
                minDistance = distance
                closestPosition = recyclerView.getChildAdapterPosition(child)
            }
        }

        // 최종적으로 계산된 위치가 리스트 범위 내에 있는지 확인
        return closestPosition.coerceIn(0, items.size)
    }

    fun concatenateTexts(items: MutableList<EditScriptItem>): String {
        return items.mapNotNull { item ->
            when (item) {
                is EditScriptItem.TextItem -> item.text
                is EditScriptItem.ButtonItem -> item.sText
                else -> null
            }
        }.joinToString(separator = "")
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.INVISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewPage.text = "3/5"
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
            toolbar.buttonClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}