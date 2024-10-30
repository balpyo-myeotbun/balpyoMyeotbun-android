package com.project.balpyo.MyPage

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentMyPageCancelAccountBinding
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan


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
                    ivMypageCancelAccount.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_checked_error))
                }
                else{
                    tvMypageCancelAccount.setTextColor(requireActivity().getColor(R.color.disabled))
                    ivMypageCancelAccount.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_uncheck))
                }
            }

            val spannableEvent = SpannableString(tvMypageCancelAccountContent.text)

            val boldText1 = "영구적으로 삭제"
            val boldText2 = "즉시 파기"
            val boldText3 = "복구가 불가능"

            val start1 = spannableEvent.indexOf(boldText1)
            val end1 = start1 + boldText1.length

            val start2 = spannableEvent.indexOf(boldText2)
            val end2 = start2 + boldText2.length

            val start3 = spannableEvent.indexOf(boldText3)
            val end3 = start3 + boldText3.length

            spannableEvent.setSpan(
                StyleSpan(Typeface.BOLD),
                start1,
                end1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannableEvent.setSpan(
                StyleSpan(Typeface.BOLD),
                start2,
                end2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannableEvent.setSpan(
                StyleSpan(Typeface.BOLD),
                start3,
                end3,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )


            tvMypageCancelAccountContent.text = spannableEvent

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