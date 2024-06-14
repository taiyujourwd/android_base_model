package com.benwu.baselib.utils

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.benwu.baselib.extension.isPermissionRationale
import com.benwu.baselib.extension.isPermissionsGranted

class BaseResultLauncher<I, O>(
    caller: ActivityResultCaller,
    contract: ActivityResultContract<I, O>
) {

    private var launcher: ActivityResultLauncher<I>

    private lateinit var callback: ActivityResultCallback<O>

    init {
        launcher = caller.registerForActivityResult(contract) {
            callback.onActivityResult(it)
        }
    }

    fun launch(input: I, callback: ActivityResultCallback<O>) {
        this.callback = callback
        launcher.launch(input)
    }
}

class PermissionsResultLauncher(caller: ActivityResultCaller) {

    private var launcher: ActivityResultLauncher<Array<String>>

    private lateinit var activity: AppCompatActivity
    private lateinit var permissions: Array<String>
    private lateinit var callback: ActivityResultCallback<Int>

    init {
        launcher =
            caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                when {
                    activity.isPermissionsGranted(*permissions) -> { // 授予權限
                        callback.onActivityResult(GRANTED)
                    }

                    activity.isPermissionRationale(*permissions) -> { // 權限遭拒 向用戶解釋要求此權限原因
                        callback.onActivityResult(EXPLAINED)
                    }

                    else -> { // 權限遭拒
                        callback.onActivityResult(DENIED)
                    }
                }
            }
    }

    fun launch(
        activity: AppCompatActivity,
        permissions: Array<String>,
        callback: ActivityResultCallback<Int>
    ) {
        this.activity = activity
        this.permissions = permissions
        this.callback = callback

        launcher.launch(permissions)
    }

    companion object {
        const val GRANTED = 0 // 允許
        const val DENIED = 1 // 拒絕
        const val EXPLAINED = 2 // 拒絕且不再詢問
    }
}