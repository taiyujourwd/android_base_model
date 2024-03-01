package com.benwu.androidbase.extension

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore("preferences_data_store")
