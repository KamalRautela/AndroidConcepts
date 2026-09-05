package com.example.androidconcepts.room

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityTodoBinding

class TodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoBinding
    private lateinit var todoAdapter: TodoAdapter
    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()

        handleBackPress()

        bindUi()
    }
    private fun handleBackPress() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun bindUi() {
        binding.rvTodos.apply {
            todoAdapter = TodoAdapter(
                onToggleDone = { todo -> viewModel.toggleDone(todo) },
                onDelete = { todo -> viewModel.deleteTodo(todo) }
            )
            layoutManager = LinearLayoutManager(this@TodoActivity, LinearLayoutManager.VERTICAL,false)
            adapter = todoAdapter
        }

        binding.btnAdd.setOnClickListener {
            val title = binding.etTodoTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.insertTodo(title)
                binding.etTodoTitle.text?.clear()
            }
        }

        viewModel.todoLiveData.observe(this) { todos ->
            todoAdapter.submitList(todos)
            binding.tvEmpty.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
            binding.rvTodos.visibility = if (todos.isEmpty()) View.GONE else View.VISIBLE
        }
    }

}