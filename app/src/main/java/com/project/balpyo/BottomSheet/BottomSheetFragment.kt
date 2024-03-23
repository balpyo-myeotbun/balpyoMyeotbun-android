package  com.project.balpyo.BottomSheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.balpyo.BottomSheetAdapter.BottomSheetAdapter
import com.project.balpyo.BottomSheetData.BottomSheetItem
import com.project.balpyo.databinding.BottomsheetBinding

class BottomSheetFragment() : BottomSheetDialogFragment() {
    lateinit var binding: BottomsheetBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 바텀시트의 레이아웃을 인플레이션
        binding = BottomsheetBinding.inflate(layoutInflater)
        val items = mutableListOf<BottomSheetItem>(
            BottomSheetItem("인공지능의 역사"),BottomSheetItem("인공지능의 역사")
        )
        val adapter = BottomSheetAdapter(items)
        binding.bottomsheetRV.adapter = adapter
        binding.bottomsheetRV.layoutManager = LinearLayoutManager(this.context)
        binding.bottomsheetRV.setOnClickListener {
            Log.d("RV", "RV")
        }

        binding.bottomsheetLoadBtn.setOnClickListener {Log.d("RV", "불러오기") }
        return binding.root
    }

    // 필요한 경우 여기에 추가적인 로직 구현
}
