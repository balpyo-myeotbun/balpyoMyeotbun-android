package com.project.balpyo.Sign

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Sign.ViewModel.SignViewModel
import com.project.balpyo.databinding.FragmentSignUpCompleteBinding


class SignUpCompleteFragment : Fragment() {
    lateinit var binding: FragmentSignUpCompleteBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: SignViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[SignViewModel::class.java]
        initToolBar()
        binding.run {
            val imageViewTarget = object : DrawableImageViewTarget(ivSignupComplete) {
                override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                    super.onResourceReady(resource, transition)
                    if (resource is GifDrawable) {
                        resource.setLoopCount(1)
                    }
                }
            }
            Glide.with(requireContext()).load(R.raw.account_complete).into(imageViewTarget)
            btnSignupEmail.setOnClickListener {
                viewModel.signIn(this@SignUpCompleteFragment, mainActivity)
                findNavController().navigate(R.id.homeFragment)
            }
        }
        return binding.root
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.INVISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewPage.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.INVISIBLE
        }
    }
}