package com.project.balpyo.MyPage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    lateinit var binding: FragmentMyPageBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        mainActivity.setTransparentStatusBar()

        binding.run {
            tvMypageMainEmail.setOnClickListener {
                findNavController().navigate(R.id.myPageAccountFragment)
            }
            llMypagePersonal.setOnClickListener {
                findNavController().navigate(R.id.myPageProfileFragment)
            }
            llMypageNotice.setOnClickListener {
                openUrl("https://balpyo.notion.site/3e95ef296572437cbcb081bd4ae4e111")
            }
            llMypageQuestion.setOnClickListener {
                openUrl("https://balpyo.notion.site/b215b5368c82451cb896da99b9a3852c")
            }
            llMypageIntroduction.setOnClickListener {
                openUrl("https://balpyo.notion.site/BALPYO-2affad5c2bf740bc80ec32e7df45dc5d")
            }

        }
        return binding.root
    }

    private fun openUrl(url: String){
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(intent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.resetStatusBar()
    }
}