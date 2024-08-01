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
import com.project.balpyo.databinding.FragmentOnboarding2Binding
import com.project.balpyo.databinding.FragmentOnboarding4Binding

class Onboarding4Fragment : Fragment() {
    lateinit var binding: FragmentOnboarding4Binding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboarding4Binding.inflate(layoutInflater)
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
            Glide.with(mainActivity).load(R.raw.onboarding_flow).override(98, 136).into(imageViewTarget)

            buttonNext.setOnClickListener {
                findNavController().navigate(R.id.onboarding5Fragment)
            }
        }

        return binding.root
    }
}