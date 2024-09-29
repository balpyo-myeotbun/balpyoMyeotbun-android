package com.project.balpyo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.project.balpyo.databinding.DialogErrorBinding

interface ErrorDialogInterface {
    fun onClickYesButton(id: Int)
}
class DialogError(var manager: FragmentManager) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogErrorBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: ErrorDialogInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogErrorBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
//        var activityAuthBinding = ActivityAuthBinding.inflate(layoutInflater)

//        activityAuthBinding.root.setBackgroundResource(R.drawable.blur_background)
    }
}