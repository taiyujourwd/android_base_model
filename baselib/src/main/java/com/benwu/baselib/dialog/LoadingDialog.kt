package com.benwu.baselib.dialog

import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.databinding.DialogLoadingBinding

class LoadingDialog(private val activity: BaseActivity<*>) {

    private val binding by lazy {
        DialogLoadingBinding.inflate(LayoutInflater.from(activity), null, false)
    }

    private val dialog by lazy {
        AppCompatDialog(activity).also {
            it.setContentView(binding.root)

            it.setCanceledOnTouchOutside(false)
            it.setCancelable(false)
            it.window?.setBackgroundDrawable(null)

            it.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    activity.onBack()
                    return@setOnKeyListener true
                }

                false
            }
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