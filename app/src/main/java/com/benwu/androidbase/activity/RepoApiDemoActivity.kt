package com.benwu.androidbase.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benwu.androidbase.adapter.RepoRvAdapter
import com.benwu.androidbase.databinding.ActivityRepoApiDemoBinding
import com.benwu.androidbase.dialog_fragment.RepoDetailDialogFragment
import com.benwu.androidbase.viewmodel.RepoViewModel
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.extension.init
import com.benwu.baselib.extension.lifecycleScope
import com.benwu.baselib.extension.showBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepoApiDemoActivity : BaseActivity<ActivityRepoApiDemoBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    private val repoViewModel by viewModels<RepoViewModel>()

    private lateinit var repoRvAdapter: RepoRvAdapter

    override fun bindViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ActivityRepoApiDemoBinding.inflate(inflater)

    override fun getBundle(bundle: Bundle) {
        //
    }

    override fun initView() {
        binding.toolbar.init(mActivity)

        initRepoRv()

        binding.srl.setOnRefreshListener(this)
    }

    override fun getData() {
        repoViewModel.getRepoList()
    }

    override fun observer() {
        lifecycleScope(Lifecycle.State.RESUMED) {
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
                        retry = this@RepoApiDemoActivity::getData
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