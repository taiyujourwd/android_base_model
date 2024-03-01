package com.benwu.androidbase.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.benwu.androidbase.data.Demo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DemoModule {

    @Singleton
    @Provides
    fun provideDemoList() = listOf(
        Demo("Google", "內嵌式網頁"),
        Demo("Retrofit", "使用Retrofit取得api回傳的資料"),
        Demo("Paging 3", "使用Paging 3實現上拉加載更多資料"),
        Demo("DataStore", "使用DataStore存取資料")
    )
}