package com.project.balpyo.Script

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.project.balpyo.R
import com.project.balpyo.Script.Adapter.SubTopicAdapter
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptSubjectBinding

class ScriptSubjectFragment : Fragment() {

    lateinit var binding: FragmentScriptSubjectBinding

    var subtopicList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptSubjectBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {
            buttonRegister.setOnClickListener {
                subtopicList.add(editTextSubtopic.text.toString())
                Log.d("발표몇분", "${subtopicList}")
                editTextSubtopic.setText("")

                var subTopicAdapter = SubTopicAdapter(subtopicList)

                var spannedGridLayoutManager = SpannedGridLayoutManager(
                    orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
                    spans = 4)

                recyclerViewSubtopic.run {

                    adapter = subTopicAdapter

                    spannedGridLayoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
                        SpanSize(2, 2)
                    }

//                    layoutManager = LinearLayoutManager(context)
                    layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)

//                    layoutManager = spannedGridLayoutManager
//                    layoutManager.itemOrderIsStable = true
                }


                subTopicAdapter.itemClickListener =
                    object : SubTopicAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            subtopicList.removeAt(position)
                            recyclerViewSubtopic.adapter?.notifyDataSetChanged()
                        }
                    }
            }
            buttonNext.setOnClickListener {
                for (i in 0 until subtopicList.size) {
                    if(MyApplication.scriptSubtopic != "") {
                        MyApplication.scriptSubtopic = "${MyApplication.scriptSubtopic}, ${subtopicList[i]}"
                    } else {
                        MyApplication.scriptSubtopic = "${subtopicList[i]}"
                    }
                    Log.d("발표몇분", "${MyApplication.scriptSubtopic}")
                }

                MyApplication.scriptSubject = editTextSubject.text.toString()

                val transaction: FragmentTransaction =
                    requireActivity().supportFragmentManager.beginTransaction()
                val ScriptTimeFragment = ScriptTimeFragment()
                transaction.replace(com.project.balpyo.R.id.fragmentContainerView, ScriptTimeFragment)
                transaction.commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerViewSubtopic.adapter?.notifyDataSetChanged()
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "2/3"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
        }
    }

}