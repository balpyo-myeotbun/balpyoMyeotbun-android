package com.project.balpyo.FlowController.AddTime

import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.FlowController.AddTime.data.MultiTypeItem
import com.project.balpyo.R

class DragListener(recyclerView: RecyclerView) : View.OnDragListener {
    var rv = recyclerView
    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_LOCATION -> { //드롭 될 위치를 미리 보여줌
                //val itemHeight = v.height
                //val dragY = event.y

                //val topDivider = v.findViewById<View>(R.id.divider_top)
                val bottomDivider = v.findViewById<View>(R.id.divider_bottom)

                val viewSource = event.localState as? View //내가 드래그 한 원본 뷰, 이벤트의 localState에서 가져옴

                if(v != viewSource) {
                    bottomDivider.visibility = View.VISIBLE
                    //하단 주석 코드는 만약 topDivider도 사용할때 활용 가능
                    /*if (dragY < itemHeight) {
                        //topDivider.visibility = View.VISIBLE
                        bottomDivider.visibility = View.INVISIBLE
                    } else {
                        //topDivider.visibility = View.GONE
                        bottomDivider.visibility = View.VISIBLE
                    }*/
                }
            }
            DragEvent.ACTION_DROP -> {
                var positionTarget: Int //드롭 대상 위치, 드래그 이벤트가 발생한 뷰의 위치를 나타내기 위해 사용

                val viewSource = event.localState as? View //내가 드래그 한 원본 뷰, 이벤트의 localState에서 가져옴
                val viewId = v.id //드롭 대상이 되는 뷰, 이벤트가 발생한 뷰의 ID를 가져옴

                //리사이클러 뷰 내부 버튼
                val btnRVBreathItem = R.id.cl_breath_btn
                val btnRVPPTItem = R.id.cl_ppt_btn

                //리사이클러 뷰 내부 스크립트
                val sentenceClItem = R.id.cl_sentence

                val rvTop = R.id.topDivider
                val rvBottom = R.id.bottomDivider

                //리사이클러 뷰 바깥 버튼
                val btnBreathItem = R.id.btn_flow_controller_breath
                val btnPPTItem = R.id.btn_flow_controller_ppt

                if (viewSource != null) {
                    when (viewSource.id) {
                        btnBreathItem, btnPPTItem -> {//리사이클러 뷰 외부 버튼 일때
                            when (viewId) {
                                btnRVBreathItem, btnRVPPTItem, sentenceClItem, rvTop, rvBottom -> {
                                    val target: RecyclerView =
                                        v.parent as RecyclerView // 드롭 대상 뷰의 부모는 타겟, 리사이클러 뷰

                                    positionTarget = v.tag as Int + 1 //드롭 대상인 뷰의 위치 인덱스 + 1

                                    val adapterTarget =
                                        target.adapter as MultiTypeAdapter // 어댑터 가져옴

                                    val customListTarget =
                                        adapterTarget.getList().toMutableList() //리스트 가져옴

                                    if (viewSource.id == btnBreathItem) {
                                        if (positionTarget < 0) { //만약 드롭 할 위치가 0보다 작다면
                                            customListTarget.add(MultiTypeItem.BreathButtonItem) //드래그 한 아이템을 리스트의 마지막에 추가
                                        } else {
                                            customListTarget.add(
                                                positionTarget,
                                                MultiTypeItem.BreathButtonItem
                                            ) //드래그한 아이템을 알맞은 위치에 추가
                                        }
                                    } else {
                                        if (positionTarget < 0) { //만약 드롭 할 위치가 0보다 작다면
                                            customListTarget.add(MultiTypeItem.PPTButtonItem) //드래그 한 아이템을 리스트의 마지막에 추가
                                        } else {
                                            customListTarget.add(
                                                positionTarget,
                                                MultiTypeItem.PPTButtonItem
                                            ) //드래그한 아이템을 알맞은 위치에 추가
                                        }
                                    }
                                    adapterTarget.updateList(customListTarget) //리스트 업데이트
                                    adapterTarget.notifyDataSetChanged() // 어댑터에 알림
                                }
                            }
                        }

                        else -> { //리사이클러 뷰 내부에서 이동할 때
                            when (viewId) { //드롭 대상 뷰 아이디
                                btnRVBreathItem, btnRVPPTItem, sentenceClItem, rvTop, rvBottom -> {
                                    val target: RecyclerView =
                                        v.parent as RecyclerView //타겟은 리사이클러 뷰

                                    positionTarget = v.tag as Int //드롭 대상인 뷰의 위치 인덱스

                                    val adapterSource = rv.adapter as MultiTypeAdapter //어댑터
                                    // 여기서 rv를 사용하는 이유는 드래그 하고 있는 아이템의 원본이 화면에서 안보이게 되면 parent를 못찾음
                                    // 따라서 viewSource.parent 대신 미리 전달받은 rv를 사용함
                                    // 드래그 이벤트 리스너는 리사이클러 뷰 내부의 아이템에만 있기 때문에 parent는 무조건 rv임
                                    val positionSource = viewSource.tag as Int //드래그한 아이템의 포지션
                                    positionTarget =
                                        if (positionTarget < positionSource) positionTarget + 1 else positionTarget
                                    //드래드하고 있는 뷰가 항상 드롭되는 위치의 하단에 추가되기 위함
                                    //만약 드래그 하고 있는 뷰의 인덱스가 드롭되는 위치의 인덱스보다 작거나 동일하다면 자기 자신이 삭제되면서 드롭 되는 뷰의 실제 인덱스는 positionTarget -1이 된다.
                                    //따라서 positionTarget은 드롭 되는 위치의 바로 다음을 의미한다.

                                    //하지만 드래그 하고 있는 뷰의 인덱스가 드롭되는 위치의 인덱스보다 크다면 자기 자신이 삭제되어도
                                    //드롭 되는 뷰의 실제 인덱스는 변동이 없다.
                                    //따라서 positionTarget + 1을 해주어야 드롭되는 뷰의 하단에 위치하게 된다.

                                    val list =
                                        adapterSource.getList()[positionSource] //드래그한 아이템 가져옴
                                    val listSource =
                                        adapterSource.getList().toMutableList() //전체 리스트

                                    listSource.removeAt(positionSource) //전체 리스트에서 드래그한 아이템 원래 위치 삭제
                                    adapterSource.updateList(listSource) //전체 리스트 업데이트
                                    adapterSource.notifyDataSetChanged() //어댑터에 알림

                                    val adapterTarget =
                                        target.adapter as MultiTypeAdapter //다시 어댑터 가져옴

                                    val customListTarget =
                                        adapterTarget.getList().toMutableList() //다시 리스트 가져옴

                                    if (positionTarget < 0) { //만약 드롭 할 위치가 0보다 작다면 (리스트가 빈 경우)
                                        customListTarget.add(list) //드래그 한 아이템을 리스트에 추가
                                    } else {
                                        customListTarget.add(
                                            positionTarget,
                                            list
                                        ) //드래그한 아이템을 알맞은 위치에 추가
                                    }
                                    adapterTarget.updateList(customListTarget) //리스트 업데이트
                                    adapterTarget.notifyDataSetChanged() // 어댑터에 알림
                                }
                            }
                        }
                    }
                }
            }
            DragEvent.ACTION_DRAG_ENDED, DragEvent.ACTION_DRAG_EXITED-> {
                //val topDivider = v.findViewById<View>(R.id.divider_top)
                val bottomDivider = v.findViewById<View>(R.id.divider_bottom)
                //topDivider.visibility = View.GONE
                bottomDivider.visibility = View.INVISIBLE
            }
        }

        return true
    }
}
