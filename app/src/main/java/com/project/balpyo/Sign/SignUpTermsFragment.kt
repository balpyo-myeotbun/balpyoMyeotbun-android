package com.project.balpyo.Sign

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.balpyo.LoadingFragmentArgs
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Sign.ViewModel.SignViewModel
import com.project.balpyo.databinding.FragmentSignUpTermsBinding
import kotlin.reflect.KMutableProperty0

class SignUpTermsFragment : Fragment() {
    lateinit var binding: FragmentSignUpTermsBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: SignViewModel
    var checkService = false
    var checkPersonal = false
    var checkAge = false
    var checkMarketing = false
    var checkAll = false

    private val args: SignUpTermsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpTermsBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[SignViewModel::class.java]
        initToolBar()

        val checkImage = requireContext().getDrawable(R.drawable.ic_check_selected)
        val uncheckImage = requireContext().getDrawable(R.drawable.ic_check_unselected)
        val black = requireActivity().getColor(R.color.text)
        val disable = requireActivity().getColor(R.color.disabled)
        val primary = requireActivity().getColor(R.color.primary)

        binding.run {
            ivSignupTermsService.setOnClickListener {
                toggleCheck(ivSignupTermsService, tvSignupTermsService, ::checkService, checkImage, uncheckImage, black, disable)
            }
            ivSignupTermsPersonal.setOnClickListener {
                toggleCheck(ivSignupTermsPersonal, tvSignupTermsPersonal, ::checkPersonal, checkImage, uncheckImage, black, disable)
            }
            ivSignupTermsAge.setOnClickListener {
                toggleCheck(ivSignupTermsAge, tvSignupTermsAge, ::checkAge, checkImage, uncheckImage, black, disable)
            }
            ivSignupTermsMarketing.setOnClickListener {
                toggleCheck(ivSignupTermsMarketing, tvSignupTermsMarketing, ::checkMarketing, checkImage, uncheckImage, black, disable)
            }
            ivSignupTermsAll.setOnClickListener {
                checkAll = !checkAll
                toggleAllChecks(checkAll, checkImage, uncheckImage, black, disable, primary)
            }
            btnSignupTerms.setOnClickListener{
                if(args.isKaKao) {}
                else viewModel.signUp(this@SignUpTermsFragment, mainActivity)
            }
        }
        return binding.root
    }

    private fun toggleCheck(imageView: ImageView, textView: TextView, checkState: KMutableProperty0<Boolean>, checkImage: Drawable?, uncheckImage: Drawable?, black: Int, disable: Int) {
        if (checkState.get()) {
            imageView.setImageDrawable(uncheckImage)
            textView.setTextColor(disable)
        } else {
            imageView.setImageDrawable(checkImage)
            textView.setTextColor(black)
        }
        checkState.set(!checkState.get())
        updateAllCheckState()
        setNextButton()
    }

    private fun toggleAllChecks(checkAll: Boolean, checkImage: Drawable?, uncheckImage: Drawable?, black: Int, disable: Int, primary: Int) {
        val drawable = if (checkAll) checkImage else uncheckImage
        val textColor = if (checkAll) primary else black
        val allDrawable = if(checkAll) requireActivity().getDrawable(R.drawable.ic_checked_accept) else requireActivity().getDrawable(R.drawable.ic_unchecked_accept)

        binding.run {
            ivSignupTermsAll.setImageDrawable(allDrawable)
            tvSignupTermsAll.setTextColor(textColor)

            listOf(
                ivSignupTermsService to tvSignupTermsService,
                ivSignupTermsPersonal to tvSignupTermsPersonal,
                ivSignupTermsAge to tvSignupTermsAge,
                ivSignupTermsMarketing to tvSignupTermsMarketing
            ).forEach { (imageView, textView) ->
                imageView.setImageDrawable(drawable)
                textView.setTextColor(if (checkAll) black else disable)
            }
        }

        checkService = checkAll
        checkPersonal = checkAll
        checkAge = checkAll
        checkMarketing = checkAll

        setNextButton()
    }

    private fun updateAllCheckState() {
        checkAll = checkService && checkPersonal && checkAge && checkMarketing
        binding.ivSignupTermsAll.setImageDrawable(
            if (checkAll) requireActivity().getDrawable(R.drawable.ic_checked_accept)
            else requireActivity().getDrawable(R.drawable.ic_unchecked_accept)
        )
        binding.tvSignupTermsAll.setTextColor(
            if (checkAll) requireActivity().getColor(R.color.primary)
            else requireActivity().getColor(R.color.text)
        )
    }

    private fun setNextButton() {
        binding.btnSignupTerms.isEnabled = checkService && checkPersonal && checkAge
    }

    private fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}