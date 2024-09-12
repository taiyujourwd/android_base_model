package com.benwu.baselib.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.benwu.baselib.extension.refresh

class FragmentVpAdapter : FragmentStateAdapter {

    private var _fragmentList = mutableListOf<Fragment>()

    constructor(activity: AppCompatActivity, fragmentList: List<Fragment>) : super(activity) {
        _fragmentList.refresh(fragmentList)
    }

    constructor(fragment: Fragment, fragmentList: List<Fragment>) : super(fragment) {
        _fragmentList.refresh(fragmentList)
    }

    override fun createFragment(position: Int) = _fragmentList[position]

    override fun getItemCount() = _fragmentList.size
}