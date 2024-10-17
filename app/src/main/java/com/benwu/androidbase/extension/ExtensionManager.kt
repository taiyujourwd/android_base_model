package com.benwu.androidbase.extension

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

//region dataStore
val PER_PAGE = intPreferencesKey("perPage")

val Context.dataStore by preferencesDataStore("preferences_data_store")
//endregion