package com.project.balpyo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.project.balpyo.databinding.FragmentLoadingBinding
import kotlin.concurrent.timer

class LoadingFragment : Fragment() {

    lateinit var binding: FragmentLoadingBinding

    var commentList = mutableListOf("발표 시간을 계산하고 있어요", "사람들의 이목을 끌 수 있는 발표대본을\n열심히 만들고 있어요", "곧 만들어져요!", "원하시는 시간에 최적화된 대본을 만들어드릴게요", "발표몇분이 열심히 대본을 만들고 있어요")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoadingBinding.inflate(layoutInflater)

        binding.run {
            Glide.with(requireContext()).load(R.raw.loading).into(imageViewLoading)

            val interval = 3000L // 밀리초 단위로 3초 설정

            var i = 0

            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    textViewLoading.text = commentList[i]
                    i++
                    if (i >= commentList.size) i = 0 // i가 리스트 크기를 초과하면 다시 처음부터 시작
                    handler.postDelayed(this, interval)
                }
            }

            handler.post(runnable)
        }

        return binding.root
    }
}