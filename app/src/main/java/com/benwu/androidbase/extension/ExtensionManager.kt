package com.benwu.androidbase.extension

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

//region dataStore
val REPO = stringPreferencesKey("repo")

val Context.dataStore by preferencesDataStore("preferences_data_store")
//endregion