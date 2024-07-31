package com.project.balpyo.Storage

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.Storage.Adapter.StorageAdapter
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Storage.Adapter.SearchHistoryAdapter
import com.project.balpyo.Storage.FilterBottomSheet.FilterBottomSheetFragment
import com.project.balpyo.Storage.FilterBottomSheet.FilterBottomSheetListener
import com.project.balpyo.api.TokenManager
import com.project.balpyo.api.response.StorageListResult
import com.project.balpyo.databinding.FragmentStorageBinding
import java.util.Locale

enum class ToolbarMode {
    MAIN, EDIT, RESULT
}

enum class LayoutMode {
    MAIN, MAIN_EMPTY, RECENT, EMPTY_RECENT, RESULT, EMPTY_RESULT
}

class StorageUIViewModel : ViewModel() {
    val toolbarMode = MutableLiveData(ToolbarMode.MAIN)
    val layoutMode = MutableLiveData(LayoutMode.MAIN)
    val selectedFilter = MutableLiveData(-1)
}

class StorageFragment : Fragment(), FilterBottomSheetListener {

    lateinit var binding: FragmentStorageBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StorageViewModel
    lateinit var uiViewModel: StorageUIViewModel
    private lateinit var flowControllerViewModel: FlowControllerViewModel
    lateinit var storageAdapter: StorageAdapter

    lateinit var searchHistoryManager: SearchHistoryManager
    lateinit var searchHistoryAdapter: SearchHistoryAdapter

    // 테스트 데이터 추가
    var list : MutableList<StorageListResult> = mutableListOf()
    var searchList: MutableList<StorageListResult> = mutableListOf()
    var filterList: MutableList<StorageListResult> = mutableListOf()

    var filterPosition = -1
    var isSearchFilter = false

    val filterBottomSheet = FilterBottomSheetFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStorageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        flowControllerViewModel = ViewModelProvider(requireActivity())[FlowControllerViewModel::class.java]
        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        uiViewModel = ViewModelProvider(mainActivity)[StorageUIViewModel::class.java]

        searchHistoryManager = SearchHistoryManager(requireContext())
        setupObservers()

        // 테스트 데이터 추가
        list = createTestData()

        viewModel.storageList.observe(mainActivity) {
            binding.run {
                rvStorageMain.run {
                    storageAdapter = StorageAdapter(list)
                    if(list.isEmpty())
                        updateLayoutMode(LayoutMode.MAIN_EMPTY)
                    else
                        updateLayoutMode(LayoutMode.MAIN)

                    adapter = storageAdapter
                    layoutManager = LinearLayoutManager(mainActivity)
                    storageAdapter.itemClickListener =
                        object : StorageAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                viewModel.getStorageDetail(
                                    this@StorageFragment,
                                    mainActivity,
                                    it[position].scriptId.toInt()
                                )
                                /*if (viewModel.storageList.value?.get(position)?.voiceFilePath != null) {
                                    flowControllerViewModel.setIsEdit(true)
                                }*/
                                findNavController().navigate(R.id.storageEditDeleteFragment)
                            }
                        }
                }
                rvStorageSearchResult.run{
                    adapter = storageAdapter
                    layoutManager = LinearLayoutManager(mainActivity)
                    storageAdapter.itemClickListener =
                        object : StorageAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                viewModel.getStorageDetail(
                                    this@StorageFragment,
                                    mainActivity,
                                    it[position].scriptId.toInt()
                                )
                                /*if (viewModel.storageList.value?.get(position)?.voiceFilePath != null) {
                                    flowControllerViewModel.setIsEdit(true)
                                }*/
                                findNavController().navigate(R.id.storageEditDeleteFragment)
                            }
                        }
                }

                // 검색 버튼 클릭 이벤트 처리
                ivStorageMainSearch.setOnClickListener {
                    updateToolbarMode(ToolbarMode.EDIT)
                    val inputMethodManager =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    etStorageSearch.requestFocus()
                    inputMethodManager.showSoftInput(etStorageSearch, InputMethodManager.SHOW_IMPLICIT)
                    showSearchHistory()
                }

                // 검색 텍스트 변경 이벤트 처리
                etStorageSearch.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        updateToolbarMode(ToolbarMode.EDIT)
                        showSearchHistory()
                        ivStorageSearchDelete.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                // 포커스 변경 이벤트 처리
                etStorageSearch.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) updateToolbarMode(ToolbarMode.EDIT)
                }

                //검색 중인 텍스트 삭제 버튼 클릭 이벤트 처리
                ivStorageSearchDelete.setOnClickListener {
                    updateToolbarMode(ToolbarMode.EDIT)
                    showSearchHistory()
                    etStorageSearch.setText("")
                }

                // 검색 버튼 클릭 이벤트 처리
                tvStorageSearch.setOnClickListener {
                    updateToolbarMode(ToolbarMode.RESULT)
                    val searchText: String = etStorageSearch.text.toString()
                    searchHistoryManager.saveSearchQuery(searchText)
                    searchList.clear()
                    if (searchText.isEmpty()) {
                        updateLayoutMode(LayoutMode.EMPTY_RESULT)
                        storageAdapter.setItems(mutableListOf())
                    } else {
                        searchList = list.filter {
                            it.script?.lowercase(Locale.getDefault())?.contains(searchText.lowercase(Locale.getDefault()))
                                ?: false
                        }.toMutableList()
                        if (searchList.isEmpty()) {
                            updateLayoutMode(LayoutMode.EMPTY_RESULT)
                        } else {
                            updateLayoutMode(LayoutMode.RESULT)
                            storageAdapter.setItems(searchList)
                        }
                    }
                }

                ivStorageMainFilter.setOnClickListener {
                    isSearchFilter = false
                    filterBottomSheet.show(childFragmentManager,filterBottomSheet.tag)
                }
                ivStorageSearchFilter.setOnClickListener {
                    isSearchFilter = true
                    filterBottomSheet.show(childFragmentManager,filterBottomSheet.tag)
                }

                tvStorageRecentDelete.setOnClickListener {
                    searchHistoryManager.clearAllSearchHistory()
                    showSearchHistory()
                }
                // 뒤로가기 버튼 클릭 이벤트 처리
                ivStorageMainBack.setOnClickListener {
                    mainActivity.binding.bottomNavigation.selectedItemId = R.id.homeFragment
                }

                // 검색 모드 뒤로가기 버튼 클릭 이벤트 처리
                ivStorageSearchBack.setOnClickListener {
                    storageAdapter.setItems(list)
                    if(list.isEmpty())
                        updateLayoutMode(LayoutMode.MAIN_EMPTY)
                    else
                        updateLayoutMode(LayoutMode.MAIN)
                    updateToolbarMode(ToolbarMode.MAIN)
                }

                // 닉네임 하이라이팅 처리
                highlightNickname("발표")
            }
        }

        return binding.root
    }

    private fun setupObservers() {
        uiViewModel.toolbarMode.observe(mainActivity) { updateToolbarUI(it) }
        uiViewModel.layoutMode.observe(mainActivity) { updateLayoutUI(it) }
    }

    override fun onFilterSelected(position: Int) {
        if (filterPosition == position) {
            filterPosition = -1 // 이미 선택된 필터를 다시 클릭하면 선택 해제
            uiViewModel.selectedFilter.value = -1
        } else {
            filterPosition = position
            uiViewModel.selectedFilter.value = position
        }
        applyFilter(filterPosition, isSearchFilter)
    }

    private fun applyFilter(position: Int, isSearchFilter: Boolean) {
        val sourceList = if (isSearchFilter) searchList else list

        val filterTag = when (position) {
            0 -> "script"
            1 -> "time"
            2 -> "flow"
            3 -> "note"
            else -> ""
        }

        filterList = if (filterTag.isNotEmpty()) {
            sourceList.filter { it.tag?.contains(filterTag) == true }.toMutableList()
        } else {
            sourceList.toMutableList()
        }

        storageAdapter.setItems(filterList)

        if (filterList.isEmpty()) {
            if (isSearchFilter) updateLayoutMode(LayoutMode.EMPTY_RESULT)
            else updateLayoutMode(LayoutMode.MAIN_EMPTY)
        } else {
            if (isSearchFilter) updateLayoutMode(LayoutMode.RESULT)
            else updateLayoutMode(LayoutMode.MAIN)
        }
    }


    private fun updateToolbarUI(mode: ToolbarMode) {
        binding.run {
            when (mode) {
                ToolbarMode.EDIT -> {
                    clStorageMainToolbar.visibility = View.INVISIBLE
                    clStorageSearchToolbar.visibility = View.VISIBLE
                    tvStorageSearch.visibility = View.VISIBLE
                    ivStorageSearchFilter.visibility = View.INVISIBLE
                    etStorageSearch.background = requireContext().getDrawable(R.drawable.background_search_edit)
                }
                ToolbarMode.RESULT -> {
                    clStorageMainToolbar.visibility = View.INVISIBLE
                    clStorageSearchToolbar.visibility = View.VISIBLE
                    tvStorageSearch.visibility = View.INVISIBLE
                    ivStorageSearchFilter.visibility = View.VISIBLE
                    uiViewModel.selectedFilter.value = -1 // 선택 상태 초기화
                    etStorageSearch.background = requireContext().getDrawable(R.drawable.background_search)
                }
                ToolbarMode.MAIN -> {
                    clStorageMainToolbar.visibility = View.VISIBLE
                    clStorageSearchToolbar.visibility = View.INVISIBLE
                    uiViewModel.selectedFilter.value = -1 // 선택 상태 초기화
                }
            }
        }
    }

    private fun updateLayoutUI(mode: LayoutMode) {
        binding.run {
            when (mode) {
                LayoutMode.MAIN -> {
                    clStorageMainRv.visibility = View.VISIBLE
                    clStorageRecent.visibility = View.INVISIBLE
                    clStorageEmptyRecent.visibility = View.INVISIBLE
                    clStorageSearchResult.visibility = View.INVISIBLE
                    clStorageEmptySearch.visibility = View.INVISIBLE
                    tvStorageMainEmpty.visibility= View.INVISIBLE
                }
                LayoutMode.MAIN_EMPTY -> {
                    clStorageMainRv.visibility = View.VISIBLE
                    clStorageRecent.visibility = View.INVISIBLE
                    clStorageEmptyRecent.visibility = View.INVISIBLE
                    clStorageSearchResult.visibility = View.INVISIBLE
                    clStorageEmptySearch.visibility = View.INVISIBLE
                    tvStorageMainEmpty.visibility= View.VISIBLE
                }
                LayoutMode.RECENT -> {
                    clStorageMainRv.visibility = View.INVISIBLE
                    clStorageRecent.visibility = View.VISIBLE
                    clStorageEmptyRecent.visibility = View.INVISIBLE
                    clStorageSearchResult.visibility = View.INVISIBLE
                    clStorageEmptySearch.visibility = View.INVISIBLE
                }
                LayoutMode.EMPTY_RECENT -> {
                    clStorageMainRv.visibility = View.INVISIBLE
                    clStorageRecent.visibility = View.INVISIBLE
                    clStorageEmptyRecent.visibility = View.VISIBLE
                    clStorageSearchResult.visibility = View.INVISIBLE
                    clStorageEmptySearch.visibility = View.INVISIBLE
                }
                LayoutMode.RESULT -> {
                    clStorageMainRv.visibility = View.INVISIBLE
                    clStorageRecent.visibility = View.INVISIBLE
                    clStorageEmptyRecent.visibility = View.INVISIBLE
                    clStorageSearchResult.visibility = View.VISIBLE
                    clStorageEmptySearch.visibility = View.INVISIBLE
                }
                LayoutMode.EMPTY_RESULT -> {
                    clStorageMainRv.visibility = View.INVISIBLE
                    clStorageRecent.visibility = View.INVISIBLE
                    clStorageEmptyRecent.visibility = View.INVISIBLE
                    clStorageSearchResult.visibility = View.INVISIBLE
                    clStorageEmptySearch.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateToolbarMode(mode: ToolbarMode) {
        uiViewModel.toolbarMode.value = mode
    }

    private fun updateLayoutMode(mode: LayoutMode) {
        uiViewModel.layoutMode.value = mode
    }

    private fun showSearchHistory() {
        val searchHistory = searchHistoryManager.getSearchHistory()
        if (searchHistory.isNotEmpty()) {
            searchHistoryAdapter = SearchHistoryAdapter(searchHistory, { query ->
                binding.etStorageSearch.setText(query)
                searchHistoryManager.removeSearchQuery(query)
                binding.tvStorageSearch.performClick()
            }) { query ->
                searchHistoryManager.removeSearchQuery(query)
                showSearchHistory()
            }
            binding.rvStorageRecent.adapter = searchHistoryAdapter
            binding.rvStorageRecent.layoutManager = LinearLayoutManager(mainActivity)
            updateLayoutMode(LayoutMode.RECENT)
        }
        else{
            updateLayoutMode(LayoutMode.EMPTY_RECENT)
        }
    }

    private fun createTestData(): MutableList<StorageListResult> {
        return mutableListOf(
            StorageListResult(1,"개인과 가족생활은 개인의 존엄과 양성의 평등을 기초로 성립되고 유지되어야 하며, 국가는 이를 보장한다. 하이라이트 되는 텍스트가 3번째 줄에 나오도록 합니다.","0", TokenManager(requireContext()).getUid()!!, "테스트 대본 1", 0, "", listOf("note", "script")),
            StorageListResult(1,"하이라이트 되는 텍스트 앞에 보여줄 텍스트가 없을 경우 바로 하이라이트되는 줄을 보여줍니다","0", TokenManager(requireContext()).getUid()!!, "테스트 대본 2", 0, "", listOf("flow")),
            StorageListResult(1,"개인과 가족생활은 개인의 존엄과 양성의 평등을 기초로 성립되고 유지되어야 하며, 국가는 이를 보장한다. 하이라이트 되는 텍스트가 3번째 줄에 나오도록 합니다.","0", TokenManager(requireContext()).getUid()!!, "테스트 대본 3", 0, "", listOf("note", "time")),
            StorageListResult(1,"개인과 가족생활은 개인의 존엄과 양성의 평등을 기초로 성립되고 유지되어야 하며, 국가는 이를 보장한다. 국회는 의원의 자격을 심사하며, 의원을 징계할 수 있다. 개인과 가족생활은 개인의 존엄과 양성의 평등을 기초로 성립되고 유지되어야 하며, 국가는 이를 보장한다.","0", TokenManager(requireContext()).getUid()!!, "테스트 대본 4", 0, "", listOf("flow", "note"))
        )
    }

    private fun highlightNickname(nickName: String) {
        val spannableTitle = SpannableString(binding.tvStorageMainNickname.text)
        spannableTitle.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.primary)),
            0,
            nickName.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        binding.tvStorageMainNickname.text = spannableTitle
    }
}