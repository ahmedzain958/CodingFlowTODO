package com.zainco.codingflowtodo.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder { BY_NAME, BY_DATE }
data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferencesManager
@Inject constructor(
/*dagger injects the application context for us using this annotation @ApplicationContext*/
    @ApplicationContext
    context: Context,
) {
    private val dataStore = context.createDataStore("yser_preferences")
    val preferencesFlow: Flow<FilterPreferences> = dataStore.data
        //map fn turns normal preferences into FilterPreferences
        .catch { exception ->
            if (exception is IOException) {
                //instead of crashing, it emits default values of every FilterPreferences property
                Log.e(TAG, "Error reading preferences:", exception)
                emit(emptyPreferences())
            } else {//if it isn't an IOExceptio, we don't want to swallow it, we want it to be thrown and will be handled somewhere
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted =
                preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder, hideCompleted)
        }
    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}