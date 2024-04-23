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
import com.benwu.baselib.extension.snackbar
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
        binding.includeToolbar.toolbar.init(mActivity, "Retrofit")

        initRepoRv()

        binding.srl.setOnRefreshListener(this)
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
                    loadingDialog.toggle(!binding.srl.isRefreshing && it.isLoading)
                    if (!it.isError) return@collectLatest
                    getString(com.benwu.baselib.R.string.api_error).snackbar(binding.root).show()
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
                RepoDetailDialogFragment.newInstance(it).show(supportFragmentManager, "repo")
            }
        }
    }
}