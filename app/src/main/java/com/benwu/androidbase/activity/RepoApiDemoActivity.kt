package com.benwu.androidbase.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benwu.androidbase.adapter.RepoRvAdapter
import com.benwu.androidbase.databinding.IncludeRepoBinding
import com.benwu.androidbase.dialog_fragment.RepoDetailDialogFragment
import com.benwu.androidbase.viewmodel.RepoViewModel
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.extension.init
import com.benwu.baselib.extension.showBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepoApiDemoActivity : BaseActivity<IncludeRepoBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var adapter: RepoRvAdapter

    override fun bindViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        IncludeRepoBinding.inflate(inflater)

    override fun getBundle(bundle: Bundle) {
        //
    }

    override fun initView() {
        binding.toolbar.init(mActivity, "Retrofit")

        initRepoRv()

        binding.srl.setOnRefreshListener(this)
        binding.loading.retry { getData() }
    }

    override fun getData() {
        viewModel.getRepoList()
    }

    override fun observer() {
        viewScope {
            launch {
                viewModel.repoUiState.collectLatest { repoUiState ->
                    repoUiState.repoList?.also { adapter.submitList(it) }
                }
            }

            launch {
                viewModel.baseUiState.collectLatest {
                    if (binding.srl.isRefreshing) binding.srl.isRefreshing = it.isLoading
                    binding.loading.toggle(it.isLoading && !binding.srl.isRefreshing, it.isError)
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
        adapter = RepoRvAdapter()
        binding.rv.init(adapter)

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