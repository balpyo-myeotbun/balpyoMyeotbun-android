package com.project.balpyo.Script

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Script.Adapter.SubTopicCheckAdapter
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptCheckBinding

class ScriptCheckFragment : Fragment() {

    lateinit var binding: FragmentScriptCheckBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: GenerateScriptViewModel

    var isDialogShowing = false
    var noSuchTime = false

    var isTitleFilled = true
    var isTopicFilled = true

    var subtopicList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentScriptCheckBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(mainActivity)[GenerateScriptViewModel::class.java]

        initToolBar()
        initView()
        observeKeyboardState()
        checkEnabled()

        binding.run {
            buttonComplete.setOnClickListener {
                viewModel.generateScript(findNavController(), mainActivity)
            }

            editTextTitleCheck.addTextChangedListener {
                buttonNextKeyboard.text = "저장"
                if(editTextTitleCheck.text.isNotEmpty()){
                    isTitleFilled = true
                }
                else{
                    isTitleFilled = false
                    checkEnabled()
                }
            }

            editTextTopicCheck.addTextChangedListener {
                buttonNextKeyboard.text = "저장"
                if(editTextTopicCheck.text.isNotEmpty()){
                    isTopicFilled = true
                }
                else{
                    isTopicFilled = false
                    checkEnabled()
                }
            }

            editTextTimeCheck.setOnTouchListener { view, motionEvent ->
                // 클릭 시 실행할 동작
                if (motionEvent.action == MotionEvent.ACTION_UP && !isDialogShowing) {
                    isDialogShowing = true
                    showDialog()
                }
                true
            }

            subtopicList = MyApplication.scriptSubtopic.split(",").toMutableList()

            val subTopicAdapter = SubTopicCheckAdapter(subtopicList)

            val spannedGridLayoutManager = SpannedGridLayoutManager(
                orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
                spans = 4
            )

            recyclerViewSubtopicCheck.run {

                adapter = subTopicAdapter

                spannedGridLayoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
                    SpanSize(2, 2)
                }

//                    layoutManager = LinearLayoutManager(context)
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
                setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS)

                setOnClickListener {
                    buttonNextKeyboard.text = "등록"
                }

//                    layoutManager = spannedGridLayoutManager
//                    layoutManager.itemOrderIsStable = true
            }


            subTopicAdapter.itemClickDeleteListener =
                object : SubTopicCheckAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        subtopicList.removeAt(position)
                        recyclerViewSubtopicCheck.adapter?.notifyDataSetChanged()
                    }
                }

            subTopicAdapter.itemClickAddListener =
                object : SubTopicCheckAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        subtopicList = MyApplication.scriptSubtopic.split(",").toMutableList()
                        subtopicList = subtopicList.filter { it.isNotBlank() }.toMutableList()
                        Log.d("발표몇분", "subtopicList filter $subtopicList")
//                        subtopicList.add("")
                        Log.d("발표몇분", "subtopicList add $subtopicList")
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
            toolbar.imageViewButtonBack.visibility = View.VISIBLE
            toolbar.imageViewButtonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.text = "대본 생성"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "5/5"
            }
            toolbar.imageViewButtonBack.setOnClickListener {
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

        val spinnerMinute: NumberPicker = dialog.findViewById(R.id.spinner_minute_check)
        val spinnerSecond: NumberPicker = dialog.findViewById(R.id.spinner_second_check)
        val buttonNoSpecificTime: LinearLayout = dialog.findViewById(R.id.button_no_specific_time_check)
        val imageViewCheck: ImageView = dialog.findViewById(R.id.imageView_check)

        dialog.setOnDismissListener {
            isDialogShowing = false
            if(noSuchTime) {
                MyApplication.scriptTimeString = "원하는 발표 시간 없음"
                MyApplication.scriptTime = 180
            } else {
                Log.d("발표몇분", "${spinnerMinute.value}")
                Log.d("발표몇분", "${spinnerSecond.value}")
                val selectedTime = spinnerMinute.value*60 + spinnerSecond.value
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

    fun checkEnabled() {
        binding.run {
            subtopicList = MyApplication.scriptSubtopic.split(",").toMutableList()

            Log.d("발표몇분", "제목 : ${isTitleFilled}")
            Log.d("발표몇분", "주제 : ${isTopicFilled}")
            Log.d("발표몇분", "소주제 : ${subtopicList.size}")

            if(isTitleFilled && isTopicFilled && (subtopicList.size > 0)) {
                buttonComplete.isEnabled = true
                buttonNextKeyboard.isEnabled = true
            } else {
                buttonComplete.isEnabled = false
                buttonNextKeyboard.isEnabled = false
            }
        }
    }

    private fun observeKeyboardState() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            var originHeight = -1
            if ( binding.root.height > originHeight) {
                originHeight =  binding.root.height
            }

            val visibleFrameSize = Rect()
            binding.root.getWindowVisibleDisplayFrame(visibleFrameSize)

            val visibleFrameHeight = visibleFrameSize.bottom - visibleFrameSize.top
            val keyboardHeight = originHeight - visibleFrameHeight

            if (keyboardHeight > visibleFrameHeight * 0.15) {
                // 키보드가 올라옴
                binding.buttonNextKeyboard.visibility = View.VISIBLE
                binding.buttonComplete.visibility = View.GONE
                binding.buttonNextKeyboard.translationY = - keyboardHeight.toFloat() // 버튼을 키보드 위로 이동
            } else {
                // 키보드가 내려감
                binding.buttonNextKeyboard.visibility = View.GONE
                binding.buttonComplete.visibility = View.VISIBLE
            }
        }
    }
}