package com.benwu.baselib.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import com.benwu.baselib.databinding.DialogLoadingBinding

class LoadingDialog(private val context: Context) {

    private val binding by lazy {
        DialogLoadingBinding.inflate(LayoutInflater.from(context), null, false)
    }

    private val dialog by lazy {
        AppCompatDialog(context).also {
            it.setContentView(binding.root)
            it.setCanceledOnTouchOutside(false)
            it.setCancelable(false)
            it.window?.setBackgroundDrawable(null)
        }
    }

    private fun show() {
        if (!dialog.isShowing) dialog.show()
    }

    private fun hide() {
        if (dialog.isShowing) dialog.dismiss()
    }

    fun toggle(isShow: Boolean) {
        if (isShow) show() else hide()
    }
}