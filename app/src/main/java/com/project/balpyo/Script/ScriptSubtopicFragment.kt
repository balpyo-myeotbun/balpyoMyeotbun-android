package com.project.balpyo.Script

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.project.balpyo.R
import com.project.balpyo.Script.Adapter.SubTopicAdapter
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptSubtopicBinding

class ScriptSubtopicFragment : Fragment() {

    lateinit var binding: FragmentScriptSubtopicBinding

    var subtopicList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptSubtopicBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {

            buttonRegister.visibility = View.INVISIBLE
            editTextChangeListener()

            buttonRegister.setOnClickListener {
                if(subtopicList.size < 5) {
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
                } else {
                    Toast.makeText(requireContext(), "소주제는 5개까지 등록할 수 있어요", Toast.LENGTH_LONG).show()
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

                findNavController().navigate(R.id.scriptTimeFragment)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.run {
            MyApplication.scriptSubtopic = ""

            recyclerViewSubtopic.run {

                var subTopicAdapter = SubTopicAdapter(subtopicList)

                adapter = subTopicAdapter

                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)

            }
        }
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.text = "대본 생성"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "3/5"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }

    fun editTextChangeListener() {
        binding.run {
            editTextSubtopic.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    TODO("Not yet implemented")
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    TODO("Not yet implemented")
                    editTextSubtopic.run {
                        if(text.toString() != "") {
                            buttonRegister.visibility = View.VISIBLE
                        } else {
                            buttonRegister.visibility = View.INVISIBLE
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}