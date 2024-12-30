package com.project.balpyo.Script

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.Script.ViewModel.ScriptDetailViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentScriptResultBinding


class ScriptResultFragment : Fragment() {

    lateinit var binding: FragmentScriptResultBinding
    lateinit var mainActivity: MainActivity

    lateinit var scriptViewModel: ScriptDetailViewModel

    var editable = false

    var title = ""
    var secTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScriptResultBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        scriptViewModel = ViewModelProvider(mainActivity)[ScriptDetailViewModel::class.java]

        scriptViewModel.run {
            scriptResult.observe(viewLifecycleOwner) {
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
            buttonStore.setOnClickListener {
                // 보관하기
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
        binding.toolbar.run {
            imageViewButtonBack.visibility = View.INVISIBLE
            imageViewButtonClose.visibility = View.VISIBLE
            textViewPage.visibility = View.INVISIBLE
            textViewTitle.run {
                visibility = View.VISIBLE
                text = "대본 생성"
            }

            imageViewButtonClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}