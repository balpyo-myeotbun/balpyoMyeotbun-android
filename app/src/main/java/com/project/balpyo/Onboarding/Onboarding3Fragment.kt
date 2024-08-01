package com.project.balpyo.Onboarding

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentOnboarding3Binding

class Onboarding3Fragment : Fragment() {
    lateinit var binding: FragmentOnboarding3Binding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboarding3Binding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {

            val imageViewTarget = object : DrawableImageViewTarget(imageView) {
                override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                    super.onResourceReady(resource, transition)
                    if (resource is GifDrawable) {
                        resource.setLoopCount(1)
                    }
                }
            }
            Glide.with(mainActivity).load(R.raw.onboarding_timecalculator).override(120, 120).into(imageViewTarget)

            buttonNext.setOnClickListener {
                findNavController().navigate(R.id.onboarding4Fragment)
            }
        }

        return binding.root
    }
}