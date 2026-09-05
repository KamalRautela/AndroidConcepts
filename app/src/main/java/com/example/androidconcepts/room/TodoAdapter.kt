package com.example.androidconcepts.room

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidconcepts.databinding.ItemTodoRowBinding

class TodoAdapter(
    private val onToggleDone: (Todo) -> Unit,
    private val onDelete: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    private var todos: List<Todo> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newTodos: List<Todo>) {
        todos = newTodos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTodoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int = todos.size

    inner class ViewHolder(private val binding: ItemTodoRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo) {
            binding.tvTodoTitle.text = todo.title
            binding.cbDone.isChecked = todo.isDone
            binding.cbDone.setOnClickListener { onToggleDone(todo) }
            binding.btnDeleteTodo.setOnClickListener { onDelete(todo) }
        }
    }
}
