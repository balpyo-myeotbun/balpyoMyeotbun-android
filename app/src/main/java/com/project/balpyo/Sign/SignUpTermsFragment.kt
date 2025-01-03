package com.project.balpyo.Sign

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Sign.ViewModel.SignViewModel
import com.project.balpyo.databinding.FragmentSignUpTermsBinding
import kotlin.reflect.KMutableProperty0

class SignUpTermsFragment : Fragment() {
    lateinit var binding: FragmentSignUpTermsBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: SignViewModel
    lateinit var serviceTerms : String
    lateinit var personalTerms : String
    private var checkService = false
    private var checkPersonal = false
    private var checkAge = false
    private var checkMarketing = false
    private var checkAll = false

    private val args: SignUpTermsFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val serviceTermsStream = resources.openRawResource(R.raw.service_term)
        serviceTerms = serviceTermsStream.bufferedReader().use { it.readText() }

        val personalTermsStream = resources.openRawResource(R.raw.service_term)
        personalTerms = personalTermsStream.bufferedReader().use { it.readText() }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpTermsBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[SignViewModel::class.java]
        initToolBar()

        val checkImage = requireContext().getDrawable(R.drawable.ic_check_selected)
        val uncheckImage = requireContext().getDrawable(R.drawable.ic_uncheck)
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

            tvSignupTermsService.setOnClickListener {
                val action = SignUpTermsFragmentDirections.actionSignUpTermsFragmentToSignUpTermsAllFragment(
                    title = "서비스 이용약관",
                    content = serviceTerms
                )
                findNavController().navigate(action)
            }
            tvSignupTermsPersonal.setOnClickListener {
                val action = SignUpTermsFragmentDirections.actionSignUpTermsFragmentToSignUpTermsAllFragment(
                    title = "개인정보 수집 및 이용",
                    content = personalTerms
                )
                findNavController().navigate(action)
            }

            btnSignupTerms.setOnClickListener{
                when (args.type) {
                    "kakao" -> {
                        Log.d("SignUp", args.type.toString())
                    }
                    "google" -> {
                        Log.d("SignUp", args.type.toString())
                    }
                    else -> {
                        viewModel.signUp(this@SignUpTermsFragment, mainActivity)
                    }
                }
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
            toolbar.imageViewButtonBack.visibility = View.VISIBLE
            toolbar.imageViewButtonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.VISIBLE
            toolbar.textViewPage.text = "3/3"
            toolbar.textViewTitle.visibility = View.INVISIBLE
            toolbar.imageViewButtonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}