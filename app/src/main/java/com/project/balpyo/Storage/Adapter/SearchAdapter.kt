package com.project.balpyo.Storage.Adapter

import android.content.Context
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.R
import com.project.balpyo.api.data.Tag
import com.project.balpyo.api.response.StorageListResult
import com.project.balpyo.databinding.ItemStorageBinding


class SearchAdapter (var result: List<StorageListResult>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var context: Context? = null
    var searchQuery : String = ""

    fun setItems(list: List<StorageListResult>) {
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
            ItemStorageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            title.text = highlightText(result[position].title)
            content.maxLines = 3
            content.text = highlightText(result[position].content)

            // 태그 초기화
            tagNote.visibility = View.GONE
            tagScript.visibility = View.GONE
            tagTime.visibility = View.GONE
            tagFlow.visibility = View.GONE

            result[position].tags.forEach {
                when (it) {
                    Tag.NOTE.value -> tagNote.visibility = View.VISIBLE
                    Tag.SCRIPT.value -> tagScript.visibility = View.VISIBLE
                    Tag.TIME.value -> tagTime.visibility = View.VISIBLE
                    Tag.FLOW.value -> tagFlow.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun highlightText(originalText: String): SpannableString {
        if (searchQuery.isNotEmpty()) {
            var highlightText = SpannableString(originalText)
            var startIndex = originalText.indexOf(searchQuery)
            if (startIndex != -1) {
                highlightText = SpannableString(highlightText.substring(startIndex))
                startIndex = 0
            }

            while (startIndex != -1) {
                highlightText.setSpan(
                    context?.let { BackgroundColorSpan(it.getColor(R.color.search_color)) },
                    startIndex,
                    startIndex + searchQuery.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                startIndex = highlightText.indexOf(searchQuery, startIndex + 1)
            }
            return highlightText
        } else
            return SpannableString(originalText)
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