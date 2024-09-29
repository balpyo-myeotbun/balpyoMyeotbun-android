package com.project.balpyo.MyPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentMyPageCancelAccountBinding

class MyPageCancelAccountFragment : Fragment() {
    lateinit var binding: FragmentMyPageCancelAccountBinding
    lateinit var mainActivity: MainActivity
    var checkCancel = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageCancelAccountBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initToolBar()
        binding.run {
            llMypageCancleAccount.setOnClickListener {
                checkCancel = !checkCancel
                btnMypageCancelAccount.isEnabled = checkCancel
                if(checkCancel){
                    tvMypageCancelAccount.setTextColor(requireActivity().getColor(R.color.text))
                    ivMypageCancelAccount.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_checked_gray4))
                }
                else{
                    tvMypageCancelAccount.setTextColor(requireActivity().getColor(R.color.disabled))
                    ivMypageCancelAccount.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_uncheck))
                }
            }
        }
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "발표몇분 탈퇴하기"
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}