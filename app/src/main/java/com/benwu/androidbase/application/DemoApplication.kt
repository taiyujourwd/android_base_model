package com.benwu.androidbase.application

import com.benwu.androidbase.activity.MainActivity
import com.benwu.baselib.application.BaseApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DemoApplication : BaseApplication() {

    override fun getHomeActivity() = MainActivity::class.java
}