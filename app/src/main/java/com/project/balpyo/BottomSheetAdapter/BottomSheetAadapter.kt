package com.project.balpyo.BottomSheetAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.BottomSheetData.BottomSheetItem
import com.project.balpyo.FlowController.data.EditScriptItem
import com.project.balpyo.R

class BottomSheetAdapter(private val items: MutableList<BottomSheetItem>) : RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {
    private var selectedPosition: Int = -1

    var onItemClick: ((BottomSheetItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottomsheet_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = items[position].title

        // 선택된 항목에 따라 테두리 색상 변경
        if (selectedPosition == position) {
            holder.title.setBackgroundResource(R.drawable.selectted_title)
        } else {
            holder.title.setBackgroundResource(R.drawable.unselected_title)
        }

        holder.itemView.setOnClickListener {
            selectedPosition = holder.bindingAdapterPosition
            notifyDataSetChanged()
            onItemClick?.invoke(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: Button = itemView.findViewById(R.id.BottomSheetBtn)
    }
}
