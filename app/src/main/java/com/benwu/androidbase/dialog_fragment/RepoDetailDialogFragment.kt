package com.benwu.androidbase.dialog_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import com.benwu.androidbase.data.Repo
import com.benwu.androidbase.databinding.DialogFragmentRepoDetailBinding
import com.benwu.baselib.dialog_fragment.BaseBottomSheetDialogFragment

class RepoDetailDialogFragment : BaseBottomSheetDialogFragment<DialogFragmentRepoDetailBinding>() {

    private var repo: Repo.Item? = null

    override fun bindViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogFragmentRepoDetailBinding.inflate(inflater, container, false)

    override fun getBundle(bundle: Bundle) {
        repo = BundleCompat.getParcelable(bundle, "data", Repo.Item::class.java)
    }

    override fun initView() {
        binding.tvTitle.text = repo?.name
        binding.tvStarCount.text = repo?.starCount?.toString()
        binding.tvContent.text = repo?.description
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

    companion object {
        fun newInstance(repo: Repo.Item) = RepoDetailDialogFragment().also { fragment ->
            fragment.arguments = Bundle().also { it.putParcelable("data", repo) }
        }
    }
}