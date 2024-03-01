package com.benwu.androidbase.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.benwu.androidbase.adapter.DemoRvAdapter
import com.benwu.androidbase.data.Demo
import com.benwu.androidbase.databinding.ActivityMainBinding
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.extension.init
import com.benwu.baselib.extension.openActivity
import com.benwu.baselib.extension.openUrl
import com.benwu.baselib.recyclerview.OnItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), OnItemClickListener<Demo> {

    @Inject
    lateinit var demoList: List<Demo>

    private lateinit var adapter: DemoRvAdapter

    override fun bindViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        ActivityMainBinding.inflate(inflater)

    override fun getBundle(bundle: Bundle) {
        //
    }

    override fun initView() {
        binding.includeToolbar.toolbar.title = "AndroidBase"

        adapter = DemoRvAdapter()
        binding.rv.init(adapter)
        adapter.submitList(demoList)
        adapter.setOnItemClickListener(this)
    }

    override fun getData() {
        //
    }

    override fun observer() {
        //
    }

    override fun onClick(v: View?) {
        //
    }

    override fun onItemClick(view: View, position: Int, data: Demo?) {
        when (data?.title) {
            "Google" -> "https://www.google.com".openUrl(
                mActivity,
                supportFragmentManager,
                data.title
            )

            "Retrofit" -> openActivity(RepoApiDemoActivity::class.java)

            "Paging 3" -> openActivity(RepoPagingDemoActivity::class.java)

            "DataStore" -> openActivity(RepoDataStoreDemoActivity::class.java)
        }
    }
}