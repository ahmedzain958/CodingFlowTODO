package com.zainco.codingflowtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.zainco.codingflowtodo.data.SortOrder
import com.zainco.codingflowtodo.data.Task
import com.zainco.codingflowtodo.data.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor(private val taskDao: TaskDao) : ViewModel() {
    val searchQuery = MutableStateFlow("")//holds a single value but can used as a flow
    //flow operator informs db by the searchquery

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    private val taskFlow: Flow<List<Task>> = combine(searchQuery, sortOrder, hideCompleted) { query, sortOrder, hideCompleted ->
        //must return all of these values, because we want to take all of them into account for this  flatMapLatest operator
        Triple(query, sortOrder, hideCompleted)
    }.flatMapLatest {( query, sortOrder, hideCompleted )->
         taskDao.getTasks(query, sortOrder, hideCompleted)
     }
    val tasks: LiveData<List<Task>> = taskFlow.asLiveData()// instead of taskDao.getTasks("bla") use taskFlow

}

