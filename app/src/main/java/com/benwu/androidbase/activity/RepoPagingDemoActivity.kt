package com.benwu.androidbase.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benwu.androidbase.adapter.RepoPagingDemoRvAdapter
import com.benwu.androidbase.databinding.IncludeRepoBinding
import com.benwu.androidbase.dialog_fragment.RepoDetailDialogFragment
import com.benwu.androidbase.viewmodel.RepoViewModel
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.adapter.FooterAdapter
import com.benwu.baselib.extension.init
import com.benwu.baselib.extension.showBottomSheetDialogFragment
import com.benwu.baselib.extension.viewScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepoPagingDemoActivity : BaseActivity<IncludeRepoBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var adapter: RepoPagingDemoRvAdapter

    override fun bindViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        IncludeRepoBinding.inflate(inflater)

    override fun getBundle(bundle: Bundle) {
        //
    }

    override fun initView() {
        binding.toolbar.init(mActivity, "Paging 3")

        initRepoRv()

        binding.srl.setOnRefreshListener(this)
    }

    @ExperimentalPagingApi
    override fun getData() {
        viewModel.getRepoPagingData()
    }

    override fun observer() {
        viewScope {
            launch {
                viewModel.repoUiState.collectLatest { repoUiState ->
                    repoUiState.repoPagingData?.also { adapter.submitData(it) }
                }
            }

            launch {
                adapter.loadStateFlow.collectLatest { loadState ->
                    val isLoading = loadState.refresh is LoadState.Loading
                    val isError = loadState.refresh is LoadState.Error
                    if (binding.srl.isRefreshing) binding.srl.isRefreshing = isLoading

                    binding.loading.toggle(
                        isLoading && !binding.srl.isRefreshing,
                        isError,
                        retry = this@RepoPagingDemoActivity::onRefresh
                    )
                }
            }
        }
    }

    override fun onClick(v: View?) {
        //
    }

    override fun onRefresh() {
        adapter.refresh()
    }

    private fun initRepoRv() {
        adapter = RepoPagingDemoRvAdapter()
        binding.rv.init(adapter.withLoadStateFooter(FooterAdapter(adapter::retry)))

        adapter.setOnItemClickListener { _, _, item ->
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