/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelab.android.datastore.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class SortOrder {
    NONE,
    BY_DEADLINE,
    BY_PRIORITY,
    BY_DEADLINE_AND_PRIORITY
}

data class UserPreferences(val showCompleted: Boolean, val sortOrder: SortOrder)

/**
 * Class that handles saving and retrieving user preferences
 */
class UserPreferencesRepository constructor(val dataStore: DataStore<Preferences>) {
    val userPreferencesFlow = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
        UserPreferences(
            it[PreferenceKeys.SHOW_COMPLETED_KEY] ?: false,
            SortOrder.valueOf(it[PreferenceKeys.SORT_ORDER_KEY] ?: SortOrder.NONE.name)
        )
    }

    suspend fun updateShowCompleted(showCompleted: Boolean) {
        dataStore.edit {
            it[PreferenceKeys.SHOW_COMPLETED_KEY] = showCompleted
        }
    }

    suspend fun enableSortByDeadline(enable: Boolean) {
        dataStore.edit {
            val currentOrder = SortOrder.valueOf(it[PreferenceKeys.SORT_ORDER_KEY] ?: SortOrder.NONE.name)
            val newSortOrder =
                if (enable) {
                    if (currentOrder == SortOrder.BY_PRIORITY) {
                        SortOrder.BY_DEADLINE_AND_PRIORITY
                    } else {
                        SortOrder.BY_DEADLINE
                    }
                } else {
                    if (currentOrder == SortOrder.BY_DEADLINE_AND_PRIORITY) {
                        SortOrder.BY_PRIORITY
                    } else {
                        SortOrder.NONE
                    }
                }
            it[PreferenceKeys.SORT_ORDER_KEY] = newSortOrder.name
        }
    }

    suspend fun enableSortByPriority(enable: Boolean) {
        dataStore.edit {
            val currentOrder = SortOrder.valueOf(it[PreferenceKeys.SORT_ORDER_KEY] ?: SortOrder.NONE.name)
            val newSortOrder =
                if (enable) {
                    if (currentOrder == SortOrder.BY_DEADLINE) {
                        SortOrder.BY_DEADLINE_AND_PRIORITY
                    } else {
                        SortOrder.BY_PRIORITY
                    }
                } else {
                    if (currentOrder == SortOrder.BY_DEADLINE_AND_PRIORITY) {
                        SortOrder.BY_DEADLINE
                    } else {
                        SortOrder.NONE
                    }
                }
            it[PreferenceKeys.SORT_ORDER_KEY] = newSortOrder.name
        }
    }

}
