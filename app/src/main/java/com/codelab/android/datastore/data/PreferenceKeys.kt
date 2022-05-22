package com.codelab.android.datastore.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {

    private const val USER_PREFERENCES_NAME = "user_preferences"
    val SHOW_COMPLETED_KEY = booleanPreferencesKey("show_completed")
    val SORT_ORDER_KEY = stringPreferencesKey("sort_order")

}