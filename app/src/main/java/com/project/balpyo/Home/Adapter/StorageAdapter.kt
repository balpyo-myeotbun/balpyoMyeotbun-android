package com.project.balpyo.Home.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.R
import com.project.balpyo.Script.Adapter.SubTopicAdapter
import com.project.balpyo.api.response.StorageListResult
import com.project.balpyo.databinding.ItemStorageBinding
import com.project.balpyo.databinding.RowStorageBinding
import com.project.balpyo.databinding.RowSubtopicBinding

class StorageAdapter (var result: List<StorageListResult>) :
    RecyclerView.Adapter<StorageAdapter.ViewHolder>() {
    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = result[position].title
    }

    override fun getItemCount() = result.size


    inner class ViewHolder(val binding: ItemStorageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val background: ConstraintLayout = itemView.findViewById(R.id.cl_item_storage)
        val title: TextView = itemView.findViewById(R.id.tv_item_storage_title)
        val content: TextView = itemView.findViewById(R.id.tv_item_storage_content)
        val timeStamp: TextView = itemView.findViewById(R.id.tv_item_storage_time)
        val tagNote : FrameLayout = itemView.findViewById(R.id.fl_item_storage_tag_note)
        val tagScript: FrameLayout = itemView.findViewById(R.id.fl_item_storage_tag_script)
        val tagTime : FrameLayout = itemView.findViewById(R.id.fl_item_storage_tag_time)
        val tagFlow : FrameLayout = itemView.findViewById(R.id.fl_item_storage_tag_flow)

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
                true
            }
        }
    }

}