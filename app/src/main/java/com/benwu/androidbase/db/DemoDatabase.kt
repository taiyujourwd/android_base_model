package com.benwu.androidbase.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.benwu.androidbase.data.RemoteKey
import com.benwu.androidbase.data.Repo

@Database(entities = [RemoteKey::class, Repo.Item::class], version = 1, exportSchema = false)
abstract class DemoDatabase : RoomDatabase() {

    abstract fun getRemoteKeyDao(): RemoteKeyDao

    abstract fun getRepoDao(): RepoDao
}