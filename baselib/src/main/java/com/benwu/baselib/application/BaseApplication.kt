package com.benwu.baselib.application

import android.app.ActivityManager
import android.app.Application
import androidx.activity.SystemBarStyle
import com.benwu.baselib.extension.isNullOrEmpty

abstract class BaseApplication : Application() {

    private var homeActivityName = "" // 首頁名稱
    private var topActivityName = ""  // 目前畫面名稱

    override fun onCreate() {
        super.onCreate()

        val tasks = getSystemService(ActivityManager::class.java).appTasks

        homeActivityName = getHomeActivity().name

        // 取得目前畫面名稱
        topActivityName = if (isNullOrEmpty(tasks)) {
            homeActivityName
        } else {
            tasks[0].taskInfo.topActivity?.className ?: homeActivityName
        }
    }

    /**
     * 取得首頁
     *
     * @return 首頁
     */
    abstract fun getHomeActivity(): Class<*>

    //region 額外方法
    /**
     * 取得狀態欄樣式
     * @return 狀態欄樣式
     */
    open fun getStatusBarStyle(): SystemBarStyle? = null

    /**
     * 取得導覽列樣式
     * @return 導覽列樣式
     */
    open fun getNavigationBarStyle(): SystemBarStyle? = null

    /**
     * 是否顯示明亮的狀態欄
     * @return 是否顯示明亮的狀態欄
     */
    open fun isAppearanceLightStatusBars(): Boolean? = null

    /**
     * 解決當使用者撤銷權限 app重啟不會回首頁
     *
     * @return app重啟畫面是否為首頁
     */
    fun isStartWithHome(): Boolean {
        if (isNullOrEmpty(homeActivityName) || topActivityName == homeActivityName) return true
        topActivityName = homeActivityName
        return false
    }
    //endregion
}