package com.project.balpyo.BottomSheetAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.BottomSheetData.BottomSheetItem
import com.project.balpyo.FlowController.data.EditScriptItem
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R

class BottomSheetAdapter(private val items: MutableList<BottomSheetItem>, var scriptId: MutableList<Long>) : RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {
    private var selectedPosition: Int = -1

    var onItemClick: ((BottomSheetItem) -> Unit)? = null

    lateinit var viewModel: StorageViewModel
    lateinit var mainActivity: MainActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottomsheet_item, parent, false)
        mainActivity = MainActivity()

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = items[position].title

        // 선택된 항목에 따라 테두리 색상 변경
        if (selectedPosition == position) {
            viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
            holder.title.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.selectted_title)
            viewModel.getStorageDetailForBottomSheet(mainActivity, scriptId.get(position).toInt())
        } else {
            holder.title.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.unselected_title)
        }

        // itemView에 대한 클릭 리스너
        holder.itemView.setOnClickListener {
            handleItemClick(position, holder)
        }

        // Button에 대한 클릭 리스너 추가
        holder.title.setOnClickListener {
            handleItemClick(position, holder)
        }
    }

    private fun handleItemClick(position: Int, holder: ViewHolder) {
        selectedPosition = position
        notifyDataSetChanged()  // 특정 아이템을 갱신하는 것으로 최적화 가능
        onItemClick?.invoke(items[position])
    }


    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: Button = itemView.findViewById(R.id.BottomSheetBtn)
    }
}
