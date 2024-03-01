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
import com.benwu.androidbase.data.Repo
import com.benwu.androidbase.databinding.IncludeRepoBinding
import com.benwu.androidbase.dialog_fragment.RepoDetailDialogFragment
import com.benwu.androidbase.viewmodel.RepoViewModel
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.adapter.FooterAdapter
import com.benwu.baselib.extension.e
import com.benwu.baselib.extension.init
import com.benwu.baselib.extension.snackbar
import com.benwu.baselib.recyclerview.OnItemClickListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepoPagingDemoActivity : BaseActivity<IncludeRepoBinding>(),
    SwipeRefreshLayout.OnRefreshListener, OnItemClickListener<Repo.Item> {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var adapter: RepoPagingDemoRvAdapter

    override fun bindViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        IncludeRepoBinding.inflate(inflater)

    override fun getBundle(bundle: Bundle) {
        //
    }

    override fun initView() {
        binding.includeToolbar.toolbar.init(mActivity, "Paging 3")

        adapter = RepoPagingDemoRvAdapter()
        binding.rv.init(adapter.withLoadStateFooter(FooterAdapter { adapter.retry() }))
        adapter.setOnItemClickListener(this)

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
                    if (binding.srl.isRefreshing) binding.srl.isRefreshing = isLoading
                    loadingDialog.toggle(!binding.srl.isRefreshing && isLoading)

                    if (loadState.refresh is LoadState.Error) {
                        getString(com.benwu.baselib.R.string.api_error).snackbar(
                            binding.root,
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(com.benwu.baselib.R.string.retry) {
                            adapter.refresh()
                        }.show()
                    }
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

    override fun onItemClick(view: View, position: Int, data: Repo.Item?) {
        data?.also { RepoDetailDialogFragment.newInstance(it).show(supportFragmentManager, "repo") }
    }
}