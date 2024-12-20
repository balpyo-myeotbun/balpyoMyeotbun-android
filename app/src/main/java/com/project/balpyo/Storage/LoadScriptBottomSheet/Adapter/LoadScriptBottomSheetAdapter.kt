package com.project.balpyo.Storage.LoadScriptBottomSheet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.Storage.LoadScriptBottomSheet.BottomSheetListener
import com.project.balpyo.Storage.LoadScriptBottomSheet.Data.BottomSheetData
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
class LoadScriptBottomSheetAdapter(
    private val items: MutableList<BottomSheetData>,
    var scriptId: MutableList<Long>,
    var viewModel: StorageViewModel,
    var mainActivity: MainActivity,
    var listener: BottomSheetListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPosition: Int = -1

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bottomsheet_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_storage, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val actualPosition = position - 1
            val item = items[actualPosition]

            holder.title.text = item.title
            holder.content.text = getFirst20CharsIgnoringSpaces(item.content)
            holder.timeStamp.text = item.timeStamp
            holder.tagNote.visibility = View.VISIBLE

            if (selectedPosition == position) {
                holder.background.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.selectted_title)
            } else {
                holder.background.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.unselected_title)
            }

            holder.background.setOnClickListener {
                handleItemClick(position)
            }
        }
    }

    private fun handleItemClick(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
        listener.onItemClicked(position - 1)
    }

    //공백을 세지 않고 20자로 제한하는 함수
    fun getFirst20CharsIgnoringSpaces(input: String): String {
        val result = StringBuilder()
        var count = 0

        for (char in input) {
            if (!char.isWhitespace()) {
                count++
            }
            result.append(char)
            if (count == 20) break
        }

        return result.toString()
    }

    override fun getItemCount(): Int = items.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val background: ConstraintLayout = itemView.findViewById(R.id.cl_item_storage)
        val title: TextView = itemView.findViewById(R.id.tv_item_storage_title)
        val content: TextView = itemView.findViewById(R.id.tv_item_storage_content)
        val timeStamp: TextView = itemView.findViewById(R.id.tv_item_storage_time)
        val tagNote : FrameLayout = itemView.findViewById(R.id.fl_item_storage_tag_note)
    }
}