package com.benwu.androidbase.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.benwu.androidbase.data.RemoteKey

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_key WHERE `table` = :query")
    suspend fun getRemoteKey(query: String): RemoteKey

    @Query("DELETE FROM remote_key WHERE `table` = :query")
    suspend fun delete(query: String)
}