package com.benwu.androidbase.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.benwu.androidbase.data.Repo

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg repo: Repo.Item)

    @Query("SELECT * FROM repo ORDER BY repo.starCount DESC")
    fun getRepoList(): PagingSource<Int, Repo.Item>

    @Query("DELETE FROM repo")
    suspend fun deleteAll()
}