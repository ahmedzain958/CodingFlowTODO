package com.zainco.codingflowtodo.ui.tasks

import android.util.Pair
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zainco.codingflowtodo.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {
    val searchQuery = MutableStateFlow("")//holds a single value but can used as a flow
    //flow operator informs db by the searchquery

    val preferencesFlow: Flow<FilterPreferences> = preferencesManager.preferencesFlow


    private val taskFlow: Flow<List<Task>> = combine(searchQuery, preferencesFlow) { query, filterPreferences ->
        //must return all of these values, because we want to take all of them into account for this  flatMapLatest operator
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }
    val tasks: LiveData<List<Task>> = taskFlow.asLiveData()// instead of taskDao.getTasks("bla") use taskFlow

    fun onSortOrderSelected(sortOrder: SortOrder) {
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder)
        }
    }

    fun onHideCompletedClick(hideCompleted: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }
    }

    fun onTaskSelected(task: Task) {

    }

    fun onTaskCheckedChanged(task: Task, checked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = checked)/*because all properties here are immutable */)
    }


}

