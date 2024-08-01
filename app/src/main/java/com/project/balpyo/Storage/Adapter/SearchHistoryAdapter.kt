package com.project.balpyo.Storage.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.databinding.ItemSearchHistoryBinding

class SearchHistoryAdapter(private val items: List<String>,
                           private val itemClickListener: (String) -> Unit,
                           private val deleteClickListener: (String) -> Unit) :
    RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.searchText.text = items[position]
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val binding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val searchText = binding.tvItemSearchHistory
        val deleteButton = binding.ivItemSearchHistory
        init {
            searchText.setOnClickListener {
                itemClickListener(items[adapterPosition])
            }
            deleteButton.setOnClickListener {
                deleteClickListener(items[adapterPosition])
            }
        }
    }
}