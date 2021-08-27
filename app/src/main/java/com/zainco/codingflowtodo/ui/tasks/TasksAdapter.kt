package com.zainco.codingflowtodo.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zainco.codingflowtodo.data.Task
import com.zainco.codingflowtodo.databinding.ItemTaskBinding

class TasksAdapter() : ListAdapter<Task, TasksAdapter.TasksViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding: ItemTaskBinding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),
            parent/*we want to put this item view layout to the recycler view later*/, false)/*parent:is the recyclerview itself*/
        return TasksViewHolder(binding)//recycler now knows whenever it needs a new item of the list, this is how it get one(all upper method)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TasksViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }
    }

    //telling adapter how it can detect the changes between items
    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem//because of data class task, it automatically implements he equals method

    }
}