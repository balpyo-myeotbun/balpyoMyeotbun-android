package com.project.balpyo.Script.Adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.RowSubtopicCheckBinding

class SubTopicCheckAdapter(var result: MutableList<String>) :
    RecyclerView.Adapter<SubTopicCheckAdapter.ViewHolder>() {
    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickDeleteListener: OnItemClickListener? = null
    var itemClickAddListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowSubtopicCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
   
        if(position == result.size) {
            if(result.size < 5) {
                Log.d("발표몇분", "size : ${result.size}")
                holder.addButton.visibility = View.VISIBLE
                holder.subtopic.visibility = View.GONE
                holder.deleteButton.visibility = View.GONE
            } else {
                holder.addButton.visibility = View.GONE
            }
        } else {
            holder.addButton.visibility = View.GONE
            holder.deleteButton.visibility = View.VISIBLE
            holder.subtopic.visibility = View.VISIBLE
            holder.subtopic.isEnabled = true
            holder.subtopic.setText(result[position].toString())
            holder.subtopic.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    holder.subtopic.setText(holder.subtopic.getText().toString())
                    holder.layout.setBackgroundResource(R.drawable.background_edittext)
                } else {
                    holder.layout.setBackgroundResource(R.drawable.background_edittext_focused)
                }
            })
            holder.subtopic.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // 데이터 리스트 업데이트
                    result[holder.adapterPosition] = s.toString()
                    MyApplication.scriptSubtopic = ""
                    for (i in 0 until result.size) {
                        if(MyApplication.scriptSubtopic != "") {
                            MyApplication.scriptSubtopic = "${MyApplication.scriptSubtopic}, ${result[i]}"
                        } else {
                            MyApplication.scriptSubtopic = "${result[i]}"
                        }
                        Log.d("발표몇분", "${MyApplication.scriptSubtopic}")
                    }
                }
            })
        }
    }

    override fun getItemCount() = if(result.size < 5) (result.size+1) else result.size

    inner class ViewHolder(val binding: RowSubtopicCheckBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val subtopic = binding.editTextSubtopic
        val deleteButton = binding.buttonDelete
        val addButton = binding.imageViewAdd
        val layout = binding.layoutSubtopicCheck

        init {
            // EditText를 활성화
            binding.editTextSubtopic.isEnabled = true

            binding.buttonDelete.setOnClickListener {
                itemClickDeleteListener?.onItemClick(adapterPosition)
                true
            }

            binding.imageViewAdd.setOnClickListener {
                itemClickAddListener?.onItemClick(adapterPosition)
                result.add("")
                MyApplication.scriptSubtopic = ""
                for (i in 0 until result.size) {
                    if(MyApplication.scriptSubtopic != "") {
                        MyApplication.scriptSubtopic = "${MyApplication.scriptSubtopic}, ${result[i]}"
                    } else {
                        MyApplication.scriptSubtopic = "${result[i]}"
                    }
                    Log.d("발표몇분", "${MyApplication.scriptSubtopic}")
                }
                true
            }

            binding.editTextSubtopic.setOnClickListener {
                // EditText에 포커스 주기
                binding.editTextSubtopic.requestFocus()
                // 키보드 자동으로 열기
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(binding.editTextSubtopic, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

}