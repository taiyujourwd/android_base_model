package com.benwu.androidbase.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benwu.androidbase.adapter.RepoRvAdapter
import com.benwu.androidbase.databinding.ActivityRepoDataStoreDemoBinding
import com.benwu.androidbase.dialog_fragment.RepoDetailDialogFragment
import com.benwu.androidbase.extension.PER_PAGE
import com.benwu.androidbase.extension.dataStore
import com.benwu.androidbase.viewmodel.RepoViewModel
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.extension.getOrDefault
import com.benwu.baselib.extension.hideKeyboard
import com.benwu.baselib.extension.init
import com.benwu.baselib.extension.lifecycleScope
import com.benwu.baselib.extension.showBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepoDataStoreDemoActivity : BaseActivity<ActivityRepoDataStoreDemoBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    private val repoViewModel by viewModels<RepoViewModel>()

    private lateinit var repoRvAdapter: RepoRvAdapter

    private var perPage = 0

    override fun bindViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ActivityRepoDataStoreDemoBinding.inflate(inflater)

    override fun getBundle(bundle: Bundle) {
        //
    }

    override fun initView() {
        binding.toolbar.init(mActivity)

        initRepoRv()

        binding.etPerPage.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                hideKeyboard(mActivity, view)

                lifecycleScope(Lifecycle.State.CREATED) {
                    launch {
                        dataStore.edit {
                            it[PER_PAGE] = binding.etPerPage.text?.toString()
                                ?.toIntOrNull()
                                .getOrDefault(0)
                        }
                    }
                }

                return@setOnEditorActionListener true
            }

            false
        }

        binding.srl.setOnRefreshListener(this)
    }

    override fun getData() {
        if (perPage > 0) {
            repoViewModel.getRepoList(perPage)
        } else {
            repoRvAdapter.submitList(emptyList())
        }
    }

    override fun observer() {
        val dataStoreSharedFlow = dataStore.data.shareIn(lifecycleScope, SharingStarted.Lazily)

        lifecycleScope(Lifecycle.State.CREATED) {
            launch {
                dataStore.data.collectLatest {
                    perPage = it[PER_PAGE].getOrDefault(0)
                    getData()
                }
            }
        }

        lifecycleScope(Lifecycle.State.RESUMED) {
            launch {
                dataStoreSharedFlow.collectLatest {
                    binding.etPerPage.setText(perPage.toString())
                }
            }

            launch {
                repoViewModel.repoSharedFlow.collectLatest { state ->
                    state.repoList?.also { repoRvAdapter.submitList(it) }
                }
            }

            launch {
                repoViewModel.baseStateFlow.collectLatest {
                    if (binding.srl.isRefreshing) binding.srl.isRefreshing = it.isLoading

                    binding.loading.toggle(
                        it.isLoading && !binding.srl.isRefreshing,
                        it.isError,
                        retry = this@RepoDataStoreDemoActivity::getData
                    )
                }
            }
        }
    }

    override fun onClick(v: View?) {
        //
    }

    override fun onRefresh() {
        getData()
    }

    private fun initRepoRv() {
        repoRvAdapter = RepoRvAdapter()
        binding.rv.init(repoRvAdapter)

        repoRvAdapter.setOnItemClickListener { _, _, item ->
            item?.also {
                showBottomSheetDialogFragment(
                    RepoDetailDialogFragment.newInstance(it),
                    supportFragmentManager,
                    "repo"
                )
            }
        }
    }
}