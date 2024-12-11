package com.project.balpyo.Script

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.balpyo.MainActivity
import com.project.balpyo.NotificationActivity
import com.project.balpyo.Script.ViewModel.GenerateScriptViewModel
import com.project.balpyo.Script.ViewModel.ScriptDetailViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptResultBinding


class ScriptResultFragment : Fragment() {

    lateinit var binding: FragmentScriptResultBinding
    lateinit var notificationActivity: NotificationActivity
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: GenerateScriptViewModel
    lateinit var scriptViewModel: ScriptDetailViewModel

    var editable = false

    var title = ""
    var secTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScriptResultBinding.inflate(layoutInflater)
        notificationActivity = activity as NotificationActivity
        mainActivity = MainActivity()

        scriptViewModel = ViewModelProvider(notificationActivity)[ScriptDetailViewModel::class.java]
        viewModel = ViewModelProvider(notificationActivity)[GenerateScriptViewModel::class.java]

        scriptViewModel.run {
            scriptResult.observe(notificationActivity) {
                title = it?.title ?: ""
                secTime = (it?.secTime ?: -1).toLong()

                binding.run {
                    editTextScript.setText(it.content)
                    val minute = (it.secTime ?: -1) / 60
                    val second = (it.secTime ?: -1) % 60

                    textViewGoalTime.text = "${minute}분 ${second}초"
                    textViewScriptTitle.text = "${it.title}"
                }
                Log.d("발표몇분", "${it}")
            }
        }

        initToolBar()
        binding.editTextScript.isFocusableInTouchMode = false
        MyApplication.scriptGenerating = false

        binding.run {
            /*
            buttonEdit.setOnClickListener {
                if (editable) {
                    buttonEdit.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.white)
                    buttonEdit.text = "대본 수정하기"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray3))
                    editTextScript.isFocusableInTouchMode = false
                } else {
                    buttonEdit.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.gray2)
                    buttonEdit.text = "대본 수정 완료"
                    buttonEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                    editTextScript.isFocusableInTouchMode = true
                }
                editable = !editable
            }
            */

            buttonStore.setOnClickListener {
                //TODO: script 생성과 동시에 보관함 저장이라 수정or삭제 필요할 것 같아요!
            // storeScript()
            }

            editTextScript.setOnLongClickListener {
                copyStr(requireContext(), editTextScript.text.toString())
            }
        }
        return binding.root
    }

    private fun copyStr(context : Context, str : String): Boolean {
        val clipboardManager : ClipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("copyScript", str)
        clipboardManager.setPrimaryClip(clipData)

        return true
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.INVISIBLE
            toolbar.buttonClose.visibility = View.VISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.run {
                visibility = View.VISIBLE
                text = "대본 생성"
            }

            toolbar.buttonClose.setOnClickListener {
                // 닫기 버튼 클릭시 동작
                /*val transaction: FragmentTransaction =
                    mainActivity!!.supportFragmentManager.beginTransaction()
                val homeFragment = HomeFragment()
                transaction.replace(R.id.fragmentContainerView, homeFragment)
                transaction.commit()*/
                /*var navHostFragment = mainActivity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                var navController = navHostFragment.navController
                navController.navigate(R.id.homeFragment)*/
//                val mainIntent = Intent(notificationActivity, MainActivity::class.java)
//                mainIntent.putExtra("type", "push")
//                startActivity(mainIntent)

                MyApplication.type = "push"

                if(MyApplication.mainActivity) {
                    notificationActivity.finish()
                } else {
                    notificationActivity.finish()
                    val mainIntent = Intent(notificationActivity, MainActivity::class.java)
                    mainIntent.putExtra("type", "push")
                    startActivity(mainIntent)
                }
//                findNavController().popBackStack()
            }
        }
    }
}