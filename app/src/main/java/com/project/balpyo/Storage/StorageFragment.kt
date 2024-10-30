package com.project.balpyo.Storage

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.balpyo.FlowController.ViewModel.FlowControllerViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Storage.Adapter.SearchAdapter
import com.project.balpyo.Storage.Adapter.SearchHistoryAdapter
import com.project.balpyo.Storage.Adapter.StorageAdapter
import com.project.balpyo.Storage.FilterBottomSheet.FilterBottomSheetFragment
import com.project.balpyo.Storage.FilterBottomSheet.FilterBottomSheetListener
import com.project.balpyo.Storage.ViewModel.StorageViewModel
import com.project.balpyo.Utils.PreferenceHelper
import com.project.balpyo.api.data.Tag
import com.project.balpyo.api.request.SearchParameter
import com.project.balpyo.api.response.StorageListResult
import com.project.balpyo.databinding.FragmentStorageBinding

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
    lateinit var searchAdapter: SearchAdapter

    lateinit var searchHistoryManager: SearchHistoryManager
    lateinit var searchHistoryAdapter: SearchHistoryAdapter

    var storageList : MutableList<StorageListResult> = mutableListOf()
    var searchList: MutableList<StorageListResult> = mutableListOf()
    var filterList: MutableList<StorageListResult> = mutableListOf()

    var filterPosition = -1
    var isSearchFilter = false

    val filterBottomSheet = FilterBottomSheetFragment()
    private lateinit var callback: OnBackPressedCallback

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

        viewModel.getStorageList(mainActivity)

        viewModel.storageList.observe(mainActivity) { list->
            storageList = list
            binding.run {
                rvStorageMain.run {
                    storageAdapter = StorageAdapter(list)
                    searchAdapter = SearchAdapter(list)
                    updateToolbarMode(ToolbarMode.MAIN)
                    if(list.isEmpty()) {
                        updateLayoutMode(LayoutMode.MAIN_EMPTY)

                    }
                    else {
                        updateLayoutMode(LayoutMode.MAIN)
                    }

                    adapter = storageAdapter
                    layoutManager = LinearLayoutManager(mainActivity)
                    storageAdapter.itemClickListener =
                        object : StorageAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                viewModel.getStorageDetail(
                                    mainActivity,
                                    list[position].id.toInt()
                                )
                                val tags = list[position].tags

                                if (tags.contains("SCRIPT") || tags.contains("FLOW") || tags.contains(
                                        "TIME"
                                    )
                                ) {
                                    if (tags.contains("SCRIPT")) {
                                        //TODO: findNavController().navigate()

                                    } else if (tags.contains("FLOW")) {
                                        flowControllerViewModel.initialize()
                                        flowControllerViewModel.setFlowControllerResult(list[position])
                                        val action = StorageFragmentDirections.actionStorageFragmentToFlowControllerResultFragment(
                                            isNew = false
                                        )
                                        findNavController().navigate(action)

                                    } else if (tags.contains("TIME")) {
                                        //TODO: findNavController().navigate()
                                    }
                                } else if (tags.contains("NOTE")) {
                                    flowControllerViewModel.initialize()
                                    flowControllerViewModel.setFlowControllerResult(list[position])
                                    val action = StorageFragmentDirections.actionStorageFragmentToFlowControllerResultFragment(
                                        isNew = false
                                    )
                                    findNavController().navigate(action)
                                } else {
                                    //TODO: 예외
                                }
                                //findNavController().navigate(R.id.storageEditDeleteFragment)
                            }
                        }
                }
                rvStorageSearchResult.run {
                    adapter = searchAdapter
                    layoutManager = LinearLayoutManager(mainActivity)
                    searchAdapter.itemClickListener =
                        object : SearchAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                viewModel.getStorageDetail(
                                    mainActivity,
                                    list[position].id.toInt()
                                )
                                val tags = list[position].tags

                                if (tags.contains("SCRIPT") || tags.contains("FLOW") || tags.contains(
                                        "TIME"
                                    )
                                ) {
                                    if (tags.contains("SCRIPT")) {
                                        //TODO: findNavController().navigate()

                                    } else if (tags.contains("FLOW")) {
                                        flowControllerViewModel.initialize()
                                        flowControllerViewModel.setFlowControllerResult(list[position])
                                        val action = StorageFragmentDirections.actionStorageFragmentToFlowControllerResultFragment(
                                            isNew = false
                                        )
                                        findNavController().navigate(action)

                                    } else if (tags.contains("TIME")) {
                                       //TODO: findNavController().navigate()
                                    }
                                } else if (tags.contains("NOTE")) {
                                    flowControllerViewModel.initialize()
                                    flowControllerViewModel.setFlowControllerResult(list[position])
                                    val action = StorageFragmentDirections.actionStorageFragmentToFlowControllerResultFragment(
                                        isNew = false
                                    )
                                    findNavController().navigate(action)
                                } else {
                                    //TODO: 예외
                                }
                            }
                        }
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
                highlightNickname()
            }
        }
        binding.run {
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
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (!s.isNullOrEmpty()) {
                        updateToolbarMode(ToolbarMode.EDIT)
                        showSearchHistory()
                        ivStorageSearchDelete.visibility =
                            if (s.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                    }
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

            llStorageMainNote.setOnClickListener {
                findNavController().navigate(R.id.noteFragment)
            }

            ivStorageMainFilter.setOnClickListener {
                isSearchFilter = false
                filterBottomSheet.show(childFragmentManager, filterBottomSheet.tag)
            }
            ivStorageSearchFilter.setOnClickListener {
                isSearchFilter = true
                filterBottomSheet.show(childFragmentManager, filterBottomSheet.tag)
            }

            tvStorageRecentDelete.setOnClickListener {
                searchHistoryManager.clearAllSearchHistory()
                showSearchHistory()
            }
            // 뒤로가기 버튼 클릭 이벤트 처리
            ivStorageMainBack.setOnClickListener {
                mainActivity.binding.bottomNavigation.selectedItemId = R.id.homeFragment
            }

            // 검색 버튼 클릭 이벤트 처리
            tvStorageSearch.setOnClickListener {
                val searchText: String = etStorageSearch.text.toString()

                viewModel.searchList.removeObservers(mainActivity)
                val searchParameter = SearchParameter(null, false, searchText)
                viewModel.searchStorageList(mainActivity, searchParameter)

                viewModel.searchList.observe(mainActivity) {
                    searchList.clear()
                    updateToolbarMode(ToolbarMode.RESULT)
                    searchHistoryManager.saveSearchQuery(searchText)
                    searchAdapter.searchQuery = searchText
                    if (searchText.isEmpty()) {
                        updateLayoutMode(LayoutMode.EMPTY_RESULT)
                        searchAdapter.setItems(mutableListOf())
                        Log.d("서치 텍스트 empty", viewModel.searchList.value.toString())
                    } else {
                        searchList = it
                        if (searchList.isEmpty()) {
                            updateLayoutMode(LayoutMode.EMPTY_RESULT)
                            Log.d("서치 리스트 empty", viewModel.searchList.value.toString())
                        } else {
                            Log.d("서치 결과 있음", viewModel.searchList.value.toString())
                            updateLayoutMode(LayoutMode.RESULT)
                            searchAdapter.setItems(searchList)
                        }
                    }
                }
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
        val sourceList = if (isSearchFilter) filterList else storageList

        val filterTag = when (position) {
            0 -> Tag.SCRIPT.value
            1 -> Tag.TIME.value
            2 -> Tag.FLOW.value
            3 -> Tag.NOTE.value
            else -> ""
        }
        val searchParameter = SearchParameter(filterTag, false, null)
        viewModel.filterStorageList(mainActivity, searchParameter)

        viewModel.filterList.observe(mainActivity) {
            filterList = if (filterTag.isNotEmpty()) {
                viewModel.filterList.value!!
            } else {
                sourceList.toMutableList()
            }
            storageAdapter.setItems(filterList)
            searchAdapter.setItems(filterList)

            if (filterList.isEmpty()) {
                if (isSearchFilter) {
                    updateLayoutMode(LayoutMode.EMPTY_RESULT)
                }
                else {
                    updateLayoutMode(LayoutMode.MAIN_EMPTY)
                }
            } else {
                if (isSearchFilter) updateLayoutMode(LayoutMode.RESULT)
                else updateLayoutMode(LayoutMode.MAIN)
            }
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
                    ivStorageSearchDelete.visibility = if(tvStorageSearch.text.isNotEmpty()) View.VISIBLE else View.INVISIBLE
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

    private fun highlightNickname() {
        val nickName = PreferenceHelper.getUserNickname(mainActivity)
        if (nickName != null) {
            binding.tvStorageMainNickname.text = "${nickName}님의 보관함"
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

    //기기의 뒤로가기 버튼을 누를 시
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.binding.bottomNavigation.selectedItemId = R.id.homeFragment
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}