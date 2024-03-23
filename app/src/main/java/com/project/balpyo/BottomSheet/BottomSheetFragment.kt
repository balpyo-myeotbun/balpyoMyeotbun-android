package  com.project.balpyo.BottomSheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.balpyo.BottomSheetAdapter.BottomSheetAdapter
import com.project.balpyo.BottomSheetData.BottomSheetItem
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.databinding.BottomsheetBinding

class BottomSheetFragment() : BottomSheetDialogFragment() {
    lateinit var binding: BottomsheetBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: StorageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 바텀시트의 레이아웃을 인플레이션
        binding = BottomsheetBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        viewModel.run {
            storageListForBottomSheet.observe(mainActivity) {
                val items = mutableListOf<BottomSheetItem>()

                var scriptId = mutableListOf<Long>()

                for (i in 0 until it.size) {
                    Log.d("발표몇분", "${it.get(i).title.toString()}")
                    items.add(BottomSheetItem(it.get(i).title.toString()))
                    Log.d("발표몇분", "${items}")

                    scriptId.add(it.get(i).scriptId)
                }

                val adapter = BottomSheetAdapter(items, scriptId)
                binding.bottomsheetRV.adapter = adapter
                binding.bottomsheetRV.layoutManager = LinearLayoutManager(mainActivity)
                binding.bottomsheetRV.setOnClickListener {
                    Log.d("발표몇분", "item click")

                    dismiss()
                }
            }
        }

        return binding.root
    }

    // 필요한 경우 여기에 추가적인 로직 구현
}
