package com.project.balpyo.Script

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.Adapter.SubTopicAdapter
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptCheckBinding

class ScriptCheckFragment : Fragment() {

    lateinit var binding: FragmentScriptCheckBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: GenerateScriptViewModel

    var subtopicList = mutableListOf(MyApplication.scriptSubtopic)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptCheckBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        /*
        viewModel = ViewModelProvider(mainActivity)[GenerateScriptViewModel::class.java]

        //알림 권한을 요청하기 위함
        //현재 이 코드 수행하지 않음
        val registerForActivityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
            when {
                deniedPermissionList.isNotEmpty() -> {
                    val map = deniedPermissionList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) ScriptTimeFragment.DENIED else ScriptTimeFragment.EXPLAINED
                    }
                    //권한이 없으면 기존처럼 로딩 프래그먼트
                    map[ScriptTimeFragment.DENIED]?.let {
                        Toast.makeText(requireContext(), "앱을 종료하지 마세요", Toast.LENGTH_LONG).show()
                        viewModel.generateScript(this@ScriptTimeFragment, mainActivity)
                        findNavController().navigate(R.id.loadingFragment)
                    }
                    map[ScriptTimeFragment.EXPLAINED]?.let {
                        Toast.makeText(requireContext(), "앱을 종료하지 마세요", Toast.LENGTH_LONG).show()
                        viewModel.generateScript(this@ScriptTimeFragment, mainActivity)
                        findNavController().navigate(R.id.loadingFragment)
                    }
                }
                else -> {
                    // 모든 권한이 허가 되었을 때
                    // 홈으로 이동
                    Toast.makeText(requireContext(), "완성이 되면 알려드릴게요!", Toast.LENGTH_LONG).show()
                    viewModel.generateScript(this@ScriptTimeFragment, mainActivity)
                    findNavController().navigate(R.id.homeFragment)
                }
            }
        }
        */

        binding.run {

            var subTopicAdapter = SubTopicAdapter(subtopicList)

            var spannedGridLayoutManager = SpannedGridLayoutManager(
                orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
                spans = 4)

            recyclerViewSubtopicCheck.run {

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
                        recyclerViewSubtopicCheck.adapter?.notifyDataSetChanged()
                    }
                }
        }

        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.text = "대본 생성"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "5/5"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }
}