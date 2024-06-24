package com.project.balpyo.Script

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.LoadingFragment
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptTimeBinding

class ScriptTimeFragment : Fragment() {

    lateinit var binding: FragmentScriptTimeBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: GenerateScriptViewModel

    var noSuchTime = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptTimeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[GenerateScriptViewModel::class.java]

        initToolBar()

        //알림 권한을 요청하기 위함
        //현재 이 코드 수행하지 않음
        val registerForActivityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
            when {
                deniedPermissionList.isNotEmpty() -> {
                    val map = deniedPermissionList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                    }
                    //권한이 없으면 기존처럼 로딩 프래그먼트
                    map[DENIED]?.let {
                        Toast.makeText(requireContext(), "앱을 종료하지 마세요",Toast.LENGTH_LONG).show()
                        viewModel.generateScript(this@ScriptTimeFragment, mainActivity)
                        findNavController().navigate(R.id.loadingFragment)
                    }
                    map[EXPLAINED]?.let {
                        Toast.makeText(requireContext(), "앱을 종료하지 마세요", Toast.LENGTH_LONG).show()
                        viewModel.generateScript(this@ScriptTimeFragment, mainActivity)
                        findNavController().navigate(R.id.loadingFragment)
                    }
                }
                else -> {
                    // 모든 권한이 허가 되었을 때
                    // 홈으로 이동
                    Toast.makeText(requireContext(), "완성이 되면 알려드릴게요!",Toast.LENGTH_LONG).show()
                    viewModel.generateScript(this@ScriptTimeFragment, mainActivity)
                    findNavController().navigate(R.id.homeFragment)
                }
            }
        }

        binding.run {
            spinnerMinute.run {
//                wrapSelectorWheel = false
                minValue = 0
                maxValue = 2
                value = 2
            }
            spinnerMinute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            spinnerSecond.run {
//                wrapSelectorWheel = false
                minValue = 0
                maxValue = 59
                value = 59
            }
            spinnerSecond.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            buttonNoSpecificTime.setOnClickListener {
                if(noSuchTime) {
                    imageViewCheck.setImageResource(R.drawable.ic_check_unselected)
                    buttonNoSpecificTime.setBackgroundResource(R.drawable.background_box_unselected)
                    noSuchTime = false
                } else {
                    imageViewCheck.setImageResource(R.drawable.ic_check_selected)
                    buttonNoSpecificTime.setBackgroundResource(R.drawable.background_box_selected)
                    noSuchTime = true
                }
            }

            buttonNext.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    /*registerForActivityResult.launch(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                    )*/

                    //대본 생성 요청 후 바로 홈으로 이동
                    Toast.makeText(requireContext(), "완성이 되면 알려드릴게요!",Toast.LENGTH_LONG).show()
                    viewModel.generateScript(this@ScriptTimeFragment, mainActivity)
                    findNavController().navigate(R.id.homeFragment)
                }
                if(noSuchTime) {
                    MyApplication.scrpitTime = 180
                } else {
                    Log.d("발표몇분", "${spinnerMinute.value}")
                    Log.d("발표몇분", "${spinnerSecond.value}")
                    var selectedTime = spinnerMinute.value*60 + spinnerSecond.value
                    MyApplication.scrpitTime = selectedTime.toLong()
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
                text = "4/5"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
    }
}