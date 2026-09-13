package com.example.androidconcepts.hilt.room

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityAnimalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimalBinding
    private lateinit var animalAdapter: AnimalAdapter
    private val viewModel: AnimalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAnimalBinding.inflate(layoutInflater)
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
        binding.rvAnimals.apply {
            animalAdapter = AnimalAdapter(
                onDelete = { animal -> viewModel.deleteAnimal(animal) }
            )
            layoutManager = LinearLayoutManager(this@AnimalActivity, LinearLayoutManager.VERTICAL, false)
            adapter = animalAdapter
        }

        binding.btnAdd.setOnClickListener {
            val name = binding.etAnimalName.text.toString().trim()
            val species = binding.etAnimalSpecies.text.toString().trim()
            if (name.isNotEmpty() && species.isNotEmpty()) {
                viewModel.insertAnimal(name, species)
                binding.etAnimalName.text?.clear()
                binding.etAnimalSpecies.text?.clear()
            }
        }

        viewModel.animalLiveData.observe(this) { animals ->
            animalAdapter.submitList(animals)
            binding.tvEmpty.visibility = if (animals.isEmpty()) View.VISIBLE else View.GONE
            binding.rvAnimals.visibility = if (animals.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}
