package com.benwu.baselib.adapter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.benwu.baselib.extension.refresh

class FragmentVpAdapter : FragmentStateAdapter {

    private var fragmentList: MutableList<Fragment> = mutableListOf()

    constructor(activity: AppCompatActivity, fragmentList: List<Fragment>) : super(activity) {
        setFragmentList(fragmentList)
    }

    constructor(fragment: Fragment, fragmentList: List<Fragment>) : super(fragment) {
        setFragmentList(fragmentList)
    }

    override fun createFragment(position: Int) = fragmentList[position]

    override fun getItemCount() = fragmentList.size

    /**
     * 設置fragmentList
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun setFragmentList(fragmentList: List<Fragment>) {
        this.fragmentList.refresh(fragmentList)
        notifyDataSetChanged()
    }
}