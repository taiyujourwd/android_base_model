package com.benwu.androidbase.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.benwu.androidbase.adapter.RepoRvAdapter
import com.benwu.androidbase.data.Repo
import com.benwu.androidbase.databinding.IncludeRepoBinding
import com.benwu.androidbase.dialog_fragment.RepoDetailDialogFragment
import com.benwu.androidbase.extension.dataStore
import com.benwu.androidbase.viewmodel.RepoViewModel
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.extension.init
import com.benwu.baselib.extension.snackbar
import com.benwu.baselib.recyclerview.OnItemClickListener

@AndroidEntryPoint
class RepoDataStoreDemoActivity : BaseActivity<IncludeRepoBinding>(),
    SwipeRefreshLayout.OnRefreshListener, OnItemClickListener<Repo.Item> {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var adapter: RepoRvAdapter

    override fun bindViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        IncludeRepoBinding.inflate(inflater)

    override fun getBundle(bundle: Bundle) {
        //
    }

    override fun initView() {
        binding.includeToolbar.toolbar.init(mActivity, "DataStore")

        adapter = RepoRvAdapter()
        binding.rv.init(adapter)
        adapter.setOnItemClickListener(this)

        binding.srl.setOnRefreshListener(this)
    }

    override fun getData() {
        viewModel.getRepoList()
    }

    override fun observer() {
        viewScope {
            launch {
                viewModel.repoUiState.collectLatest { repoUiState ->
                    repoUiState.repoList?.also { repo ->
                        dataStore.edit { it[REPO] = Gson().toJson(repo) }
                    }
                }
            }

            launch {
                dataStore.data.map { it[REPO] }.collectLatest { data ->
                    data?.also {
                        adapter.submitList(
                            Gson().fromJson(it, Array<Repo.Item>::class.java).toList()
                        )
                    }
                }
            }

            launch {
                viewModel.baseUiState.collectLatest {
                    if (binding.srl.isRefreshing) binding.srl.isRefreshing = it.isLoading
                    loadingDialog.toggle(!binding.srl.isRefreshing && it.isLoading)

                    if (it.isError) {
                        getString(com.benwu.baselib.R.string.api_error).snackbar(
                            binding.root,
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(com.benwu.baselib.R.string.retry) {
                            getData()
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
        getData()
    }

    override fun onItemClick(view: View, position: Int, data: Repo.Item?) {
        data?.also { RepoDetailDialogFragment.newInstance(it).show(supportFragmentManager, "repo") }
    }

    companion object {
        private val REPO = stringPreferencesKey("repo")
    }
}