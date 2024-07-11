package com.project.balpyo.FlowController.AddTime

import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.balpyo.R
import com.project.balpyo.FlowController.AddTime.data.MultiTypeItem

//두종류의 버튼과 텍스트뷰 총 3가지 아이템 종류를 가지는 리사이클러뷰의 어댑터
class MultiTypeAdapter(private var items: MutableList<MultiTypeItem>, private var recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnLongClickListener {
    //recyclerView 변수는 아이템 스크롤 시 부모뷰를 잃어버리기 때문에 추가한 변수
    companion object {
        private const val TYPE_TEXT = 0
        private const val TYPE_BREATH_BUTTON = 1
        private const val TYPE_PPT_BUTTON = 2
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TEXT -> TextViewHolder(layoutInflater.inflate(R.layout.item_sentence, parent, false))
            TYPE_BREATH_BUTTON -> BreathButtonViewHolder(layoutInflater.inflate(R.layout.item_breath_button, parent, false))
            TYPE_PPT_BUTTON -> PPTButtonViewHolder(layoutInflater.inflate(R.layout.item_ppt_button, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextViewHolder -> holder.bind((items[position] as MultiTypeItem.TextItem).text, this, position)
            is BreathButtonViewHolder -> holder.bind(this, position)
            is PPTButtonViewHolder -> holder.bind(this, position)
        }
    }
    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(text: String, adapter: MultiTypeAdapter, position: Int) {
            itemView.findViewById<TextView>(R.id.tv_rv_sentence).text = text
            itemView.findViewById<ConstraintLayout>(R.id.cl_sentence).tag = position
            itemView.findViewById<ConstraintLayout>(R.id.cl_sentence).setOnDragListener(DragListener(adapter.recyclerView))
        }
    }

    class BreathButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(adapter: MultiTypeAdapter, position: Int) {
            itemView.findViewById<ConstraintLayout>(R.id.cl_breath_btn).tag = position
            itemView.findViewById<ConstraintLayout>(R.id.btn_rv_breath).tag = position
            itemView.findViewById<ConstraintLayout>(R.id.btn_rv_breath).setOnLongClickListener(adapter)
            itemView.findViewById<ConstraintLayout>(R.id.cl_breath_btn).setOnDragListener(
                DragListener(
                adapter.recyclerView
            )
            )
            itemView.findViewById<ImageView>(R.id.iv_breath_delete).setOnClickListener{
                adapter.removeItemAt(position)
            }
        }
    }

    class PPTButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(adapter: MultiTypeAdapter, position: Int) {
            itemView.findViewById<ConstraintLayout>(R.id.cl_ppt_btn).tag = position
            itemView.findViewById<ConstraintLayout>(R.id.btn_rv_ppt).tag = position
            itemView.findViewById<ConstraintLayout>(R.id.btn_rv_ppt).setOnLongClickListener(adapter)
            itemView.findViewById<ConstraintLayout>(R.id.cl_ppt_btn).setOnDragListener(
                DragListener(
                adapter.recyclerView
            )
            )
            itemView.findViewById<ImageView>(R.id.iv_ppt_delete).setOnClickListener{
                adapter.removeItemAt(position)
            }
        }
    }
    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MultiTypeItem.TextItem -> TYPE_TEXT
            is MultiTypeItem.BreathButtonItem -> TYPE_BREATH_BUTTON
            is MultiTypeItem.PPTButtonItem -> TYPE_PPT_BUTTON
        }
    }

    override fun onLongClick(v: View?): Boolean {
        Log.d("", "Long")
        val data = ClipData.newPlainText("", "")
        val shadowBuilder = View.DragShadowBuilder(v)
        v!!.startDragAndDrop(data, shadowBuilder, v.parent, 0)
        //여기서 부모를 전달하는 이유는, 드래그 리스너는 버튼을 감싸는 부모 레이아웃에 달려있음
        //하지만 롱클릭 리스너는 버튼에 달려져잇음
        //따라서 리스너에게 드래그 리스너가 달린 객체와 드래그 하고 있는 객체를 동일하게 주기위해
        //v.parent를 전달
        //추후 리스너에서 v와 viewSource가 같으면 (자기 자신이면) 미리보기 보이지 않게 할 수 있음
        return true
    }

    fun getList(): MutableList<MultiTypeItem> = items

    fun updateList(list: MutableList<MultiTypeItem>) {
        this.items = list
    }

    // 아이템 제거 메소드
    fun removeItemAt(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }
    }
}