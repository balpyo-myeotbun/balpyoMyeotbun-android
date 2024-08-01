package com.project.balpyo.Sign

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentSignUpCertificationBinding

class SignUpCertificationFragment : Fragment() {
    lateinit var binding: FragmentSignUpCertificationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpCertificationBinding.inflate(layoutInflater)
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
            Handler().postDelayed({
                findNavController().navigate(R.id.signUpPasswordFragment)
            }, 3000)
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