package com.project.balpyo.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.balpyo.Home.Adapter.StorageAdapter
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentStorageBinding

class StorageFragment : Fragment() {

    lateinit var binding: FragmentStorageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStorageBinding.inflate(layoutInflater)

        initToolBar()

        binding.run {

            var typeArray = resources.getStringArray(R.array.storage_menu)	// 배열
            setSpinner(spinnerStorage, typeArray)	// 스피너 설정

            spinnerStorage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when(position) {
                        0 -> {

                        }

                        1 -> {

                        }

                        2 -> {

                        }

                        3 -> {

                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            recyclerViewList.run {
                adapter = StorageAdapter()

                layoutManager = LinearLayoutManager(requireContext())
            }
        }


        return binding.root
    }

    // 스피너 설정
    private fun setSpinner(spinner: Spinner, array: Array<String>) {
        var adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line
        ) { override fun getCount(): Int =  super.getCount() }  // array에서 hint 안 보이게 하기
        adapter.addAll(array.toMutableList())   // 배열 추가
        spinner.adapter = adapter               // 어댑터 달기
        spinner.setSelection(0)    // 스피너 초기값=hint
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
            }
        }
    }
}