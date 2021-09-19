package com.zainco.codingflowtodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when(sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedDateCreated(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    @Query("Select * FROM task_table WHERE (completed!= :hideCompleted OR completed=0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>//not list of tasks but a stream of tasks and whenever this table changes, room will automatically put a new list of task into this flow,

    @Query("Select * FROM task_table WHERE (completed!= :hideCompleted OR completed=0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTasksSortedDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>//not list of tasks but a stream of tasks and whenever this table changes, room will automatically put a new list of task into this flow,

    //flow can only be collected from coroutine, this is why we don't need the suspend modifier here(all the suspension happens inside the flow),
    // flow is an asynchronous stream of data, and instead of using flow we could use livedata and it will work pretty similar

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}