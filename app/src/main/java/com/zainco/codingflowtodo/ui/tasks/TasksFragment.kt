package com.zainco.codingflowtodo.ui.tasks

import android.app.Fragment
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zainco.codingflowtodo.R
import com.zainco.codingflowtodo.data.FilterPreferences
import com.zainco.codingflowtodo.data.SortOrder
import com.zainco.codingflowtodo.data.Task
import com.zainco.codingflowtodo.databinding.FragmentTasksBinding
import com.zainco.codingflowtodo.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint/*Fragments and Activities can't be constructor injected(different cases)*/
class TasksFragment : androidx.fragment.app.Fragment(R.layout.fragment_tasks) , TasksAdapter.OnItemClickListener{
    private val viewModel: TasksViewModel by viewModels()//injected by dagger as long as we have @AndroidEntryPoint annotation
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTasksBinding.bind(view)
        val taskAdapter = TasksAdapter(this)
        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }
        //if not called, no menu items displayed
        setHasOptionsMenu(true)
    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task,isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        //instead of this searchView.setOnQueryTextListener()
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
            //crossinline there avoids calling return here
        }
        //fragment lifecycle scope
        viewLifecycleOwner.lifecycleScope.launch {
//            val x: FilterPreferences = viewModel.preferencesFlow.first()
            menu.findItem(R.id.action_hide_completed_tasks).isChecked=
                viewModel.preferencesFlow.first().hideCompleted
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//true:item handled - false:didn't handle
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)//don't call the method updateSortOrder because it is imply that the fragment is who updates the value
                //on the contrary the fragment tells the view model hey!! the sort order is changed, you decide what to do
                true
            }
            R.id.action_sort_by_date_created -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {

                true
            }
            else -> super.onOptionsItemSelected(item)//false by system, not handled any item
        }
    }
}