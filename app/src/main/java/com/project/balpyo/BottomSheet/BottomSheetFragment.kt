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
import com.project.balpyo.R
import com.project.balpyo.databinding.BottomsheetBinding
interface BottomSheetListener {
    fun onItemClicked(position: Int)
}
class BottomSheetFragment() : BottomSheetDialogFragment(), BottomSheetListener {
    lateinit var binding: BottomsheetBinding
    lateinit var mainActivity: MainActivity
    var scriptId = mutableListOf<Long>()
    var position : Int? = null
    lateinit var viewModel: StorageViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 바텀시트의 레이아웃을 인플레이션
        binding = BottomsheetBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        viewModel.run {
            storageListForBottomSheet.observe(mainActivity) {
                val items = mutableListOf<BottomSheetItem>()

                for (i in 0 until it.size) {
                    Log.d("발표몇분", "${it.get(i).title.toString()}")
                    items.add(BottomSheetItem(it.get(i).title.toString()))
                    Log.d("발표몇분", "${items}")

                    scriptId.add(it.get(i).scriptId)
                }

                val adapter = BottomSheetAdapter(items, scriptId, viewModel, mainActivity, this@BottomSheetFragment)
                binding.bottomsheetRV.adapter = adapter
                binding.bottomsheetRV.layoutManager = LinearLayoutManager(mainActivity)
                binding.bottomsheetWriteBtn.setOnClickListener {
                    dismiss()
                }
                binding.bottomsheetLoadBtn.setOnClickListener {
                    viewModel.getStorageDetailForBottomSheet(
                            mainActivity,
                            scriptId[position!!].toInt()
                        )
                    dismiss()
                }
            }
        }

        return binding.root
    }

    override fun onItemClicked(position: Int) {
        binding.bottomsheetLoadBtn.isEnabled = true // 아이템이 클릭되면 버튼 활성화
        binding.bottomsheetLoadLayout.setBackgroundResource(R.drawable.background_load_script_selected)
        this.position = position
    }
}
