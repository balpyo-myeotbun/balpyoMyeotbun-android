package com.project.balpyo.Script.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.project.balpyo.databinding.RowSubtopicBinding


class SubTopicAdapter (var result: List<String>) :
    RecyclerView.Adapter<SubTopicAdapter.ViewHolder>() {
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
            RowSubtopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.subtopic.text = result[position].toString()
    }

    override fun getItemCount() = result.size

    inner class ViewHolder(val binding: RowSubtopicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val subtopic = binding.textViewSubtopic
        val deleteButton = binding.buttonDelete

        init {
            binding.buttonDelete.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)
                true
            }
        }
    }

}