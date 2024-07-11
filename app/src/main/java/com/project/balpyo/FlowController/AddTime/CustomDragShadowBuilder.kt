package com.project.balpyo.FlowController.AddTime

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View

class CustomDragShadowBuilder(context: Context, layoutId: Int) : View.DragShadowBuilder() {
    private val shadowView: View

    init {
        val inflater = LayoutInflater.from(context)
        shadowView = inflater.inflate(layoutId, null, false)
        //레이아웃 초기화
        shadowView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        shadowView.layout(0, 0, shadowView.measuredWidth, shadowView.measuredHeight)
    }

    override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
        // 섀도우 크기 설정
        outShadowSize.set(shadowView.width, shadowView.height)
        // 터치 포인트 설정 (중앙에 설정)
        outShadowTouchPoint.set(shadowView.width / 2, shadowView.height / 2)
    }

    override fun onDrawShadow(canvas: Canvas) {
        // 섀도우 그리기
        shadowView.draw(canvas)
    }
}