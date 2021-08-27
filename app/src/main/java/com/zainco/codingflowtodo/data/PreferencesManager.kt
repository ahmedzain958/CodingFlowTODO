package com.zainco.codingflowtodo.data

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class SortOrder { BY_NAME, BY_DATE }
data class FilterPreferences (val sortOrder: SortOrder,val hideCompleted:Boolean)
@Singleton
class PreferencesManager
@Inject constructor(
/*dagger injects the application context for us using this annotation @ApplicationContext*/
    @ApplicationContext
    context: Context,
) {
    private val dataStore = context.createDataStore("yser_preferences")
    val preferencesFlow = dataStore.data
        //map fn turns normal preferences into FilterPreferences
        .map { preferences ->
            val  sortOrder= SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted =
                preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder,hideCompleted)
        }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}