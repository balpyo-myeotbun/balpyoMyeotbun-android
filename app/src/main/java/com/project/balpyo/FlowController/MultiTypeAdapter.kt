package com.project.balpyo.FlowController

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.R
import com.project.balpyo.FlowController.data.EditScriptItem

class MultiTypeAdapter(val items: MutableList<EditScriptItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_TEXT = 0
        private const val TYPE_BUTTON = 1
        private const val TYPE_DIVIDER = 3
    }
    // 아이템 제거 메소드
    fun removeItemAt(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private var dragPosition = -1 // 드래그 위치를 나타내는 변수

    // ViewHolder 및 기타 메서드 생략

    fun showDragPosition(position: Int) {
        // 기존에 표시된 드래그 위치를 제거
        clearDragPosition()

        // 새로운 드래그 위치에 임시 아이템 추가, 리스트의 끝을 초과하지 않도록 조정
        dragPosition = position.coerceAtMost(items.size)
        if(dragPosition != -1) {
            items.add(dragPosition, EditScriptItem.Divider)
            notifyItemInserted(dragPosition)
        }
    }


    fun clearDragPosition() {
        if (dragPosition >= 0) {
            items.removeAt(dragPosition)
            notifyItemRemoved(dragPosition)
            dragPosition = -1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is EditScriptItem.TextItem -> TYPE_TEXT
            is EditScriptItem.ButtonItem -> TYPE_BUTTON
            is EditScriptItem.Divider -> TYPE_DIVIDER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TEXT -> TextViewHolder(inflater.inflate(R.layout.sentence_item, parent, false))
            TYPE_BUTTON -> ButtonViewHolder(inflater.inflate(R.layout.button_item, parent, false))
            TYPE_DIVIDER -> DividerViewHolder(inflater.inflate(R.layout.drop_item_divider, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextViewHolder -> holder.bind((items[position] as EditScriptItem.TextItem).text)
            is ButtonViewHolder -> holder.bind((items[position] as EditScriptItem.ButtonItem).text, this, position)
        }
    }

    override fun getItemCount(): Int = items.size

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(text: String) {
            itemView.findViewById<TextView>(R.id.tv_sentence).text = text
        }
    }

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(text: String,  adapter: MultiTypeAdapter, position: Int){
            itemView.findViewById<Button>(R.id.btn_breath).text = text
            itemView.findViewById<Button>(R.id.btn_breath).setOnClickListener {
                // 버튼 클릭 시 해당 아이템 제거
                adapter.removeItemAt(position)
            }
        }
    }

    class  DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(){

        }
    }
}
