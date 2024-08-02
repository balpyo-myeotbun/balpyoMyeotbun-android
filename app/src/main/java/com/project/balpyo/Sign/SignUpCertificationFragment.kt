package com.project.balpyo.Sign

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Sign.ViewModel.SignViewModel
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentSignUpCertificationBinding

class SignUpCertificationFragment : Fragment() {
    lateinit var binding: FragmentSignUpCertificationBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: SignViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpCertificationBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[SignViewModel::class.java]
        initToolBar()
        binding.run {
            val imageViewTarget = object : DrawableImageViewTarget(ivCertification) {
                override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                    super.onResourceReady(resource, transition)
                    if (resource is GifDrawable) {
                        resource.setLoopCount(1)
                    }
                }
            }
            Glide.with(requireContext()).load(R.raw.send_email).into(imageViewTarget)

            tvSignupCertificationEmail.text = MyApplication.email + tvSignupCertificationEmail.text
            btnSignupCertification.setOnClickListener {
                viewModel.signIn(this@SignUpCertificationFragment, mainActivity)
                viewModel.signInResponse.observe(mainActivity){
                    if(it.roles[0] == "ROLE_USER"){
                        findNavController().navigate(R.id.signUpCompleteFragment)
                    }
                    else{
                        Toast.makeText(requireContext(),"인증이 되지 않았어요. 발송된 메일의 링크를 눌러주세요", Toast.LENGTH_SHORT).show()
                    }
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
            toolbar.textViewTitle.visibility = View.INVISIBLE
            toolbar.buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}