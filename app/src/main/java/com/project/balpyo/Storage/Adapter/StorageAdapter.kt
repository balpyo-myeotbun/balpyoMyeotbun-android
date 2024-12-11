package com.project.balpyo.Storage.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.R
import com.project.balpyo.api.BaseDto
import com.project.balpyo.api.data.Tag
import com.project.balpyo.databinding.ItemStorageBinding


class StorageAdapter (var result: List<BaseDto>) :
    RecyclerView.Adapter<StorageAdapter.ViewHolder>() {
    private var context: Context? = null


    fun setItems(list: List<BaseDto>) {
        result = list
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            ItemStorageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            title.text = result[position].title
            content.text = getFirst20CharsIgnoringSpaces(result[position].content ?: "")

            // 태그 초기화
            tagNote.visibility = View.GONE
            tagScript.visibility = View.GONE
            tagTime.visibility = View.GONE
            tagFlow.visibility = View.GONE

            result[position].tags?.forEach {
                when (it) {
                    Tag.NOTE.value -> tagNote.visibility = View.VISIBLE
                    Tag.SCRIPT.value -> tagScript.visibility = View.VISIBLE
                    Tag.TIME.value -> tagTime.visibility = View.VISIBLE
                    Tag.FLOW.value -> tagFlow.visibility = View.VISIBLE
                }
            }
        }
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

    override fun getItemCount() = result.size


    inner class ViewHolder(val binding: ItemStorageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val background = binding.clItemStorage
        val title = binding.tvItemStorageTitle
        val content = binding.tvItemStorageContent
        val timeStamp = binding.tvItemStorageContent
        val tagNote : FrameLayout = itemView.findViewById(R.id.fl_item_storage_tag_note)
        val tagScript: FrameLayout = itemView.findViewById(R.id.fl_item_storage_tag_script)
        val tagTime : FrameLayout = itemView.findViewById(R.id.fl_item_storage_tag_time)
        val tagFlow : FrameLayout = itemView.findViewById(R.id.fl_item_storage_tag_flow)

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
            }
        }
    }

}