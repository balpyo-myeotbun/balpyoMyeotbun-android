package com.project.balpyo.Script

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
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

    var isDialogShowing = false
    var noSuchTime = false

    var subtopicList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScriptCheckBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()
        initView()

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

            subtopicList = MyApplication.scriptSubtopic.split(",").toMutableList()

            var subTopicAdapter = SubTopicAdapter(subtopicList)

            var spannedGridLayoutManager = SpannedGridLayoutManager(
                orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
                spans = 4)

            editTextTimeCheck.setOnTouchListener { view, motionEvent ->
                // 클릭 시 실행할 동작
                Log.d("발표몇분", "시간 변경")

                if (motionEvent.action == MotionEvent.ACTION_UP && !isDialogShowing) {
                    isDialogShowing = true
                    showDialog()
                }

                true
            }

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

    override fun onResume() {
        super.onResume()
        initView()
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

    fun initView() {
        binding.run {
            editTextTitleCheck.setText(MyApplication.scriptTitle)
            editTextTopicCheck.setText(MyApplication.scriptTopic)
            editTextTimeCheck.setText(MyApplication.scriptTimeString)
        }
    }

    fun showDialog() {
        val dialog = Dialog(mainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_script_time)
//        requireContext().dialogFragmentResize(dialogFragment = scriptTimeDialog, 0.9f, 0.8f)
        dialog.window?.apply {
            setBackgroundDrawable(getResources().getDrawable(R.drawable.background_dialog))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
        }

        var spinnerMinute: NumberPicker = dialog.findViewById(R.id.spinner_minute_check)
        var spinnerSecond: NumberPicker = dialog.findViewById(R.id.spinner_second_check)
        var buttonNoSpecificTime: LinearLayout = dialog.findViewById(R.id.button_no_specific_time_check)
        var imageViewCheck: ImageView = dialog.findViewById(R.id.imageView_check)

        dialog.setOnDismissListener {
            isDialogShowing = false
            if(noSuchTime) {
                MyApplication.scriptTimeString = "원하는 발표 시간 없음"
                MyApplication.scriptTime = 180
            } else {
                Log.d("발표몇분", "${spinnerMinute.value}")
                Log.d("발표몇분", "${spinnerSecond.value}")
                var selectedTime = spinnerMinute.value*60 + spinnerSecond.value
                MyApplication.scriptTime = selectedTime.toLong()
                if(spinnerMinute.value != 0) {
                    MyApplication.scriptTimeString = "${spinnerMinute.value}분 ${spinnerSecond.value}초"
                } else {
                    MyApplication.scriptTimeString = "${spinnerSecond.value}초"
                }
            }

            binding.editTextTimeCheck.setText(MyApplication.scriptTimeString)
        }

        if(MyApplication.scriptTimeString == "원하는 발표 시간 없음") {
            noSuchTime = true
            imageViewCheck.setImageResource(R.drawable.ic_check_selected)
            buttonNoSpecificTime.setBackgroundResource(R.drawable.background_box_selected)
        } else {
            noSuchTime = false
            imageViewCheck.setImageResource(R.drawable.ic_check_unselected)
            buttonNoSpecificTime.setBackgroundResource(R.drawable.background_box_unselected)
        }

        spinnerMinute.run {
//                wrapSelectorWheel = false
            minValue = 0
            maxValue = 2
            value = (MyApplication.scriptTime/60).toInt()
        }
        spinnerMinute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        spinnerSecond.run {
//                wrapSelectorWheel = false
            minValue = 0
            maxValue = 59
            value = (MyApplication.scriptTime%60).toInt()
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

        dialog.show()
    }

    private fun Context.dialogFragmentResize(
        dialogFragment: DialogFragment,
        width: Float,
        height: Float,
    ) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialogFragment.dialog?.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)
        } else {
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialogFragment.dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }
}