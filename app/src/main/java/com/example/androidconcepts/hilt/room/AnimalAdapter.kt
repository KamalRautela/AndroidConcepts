package com.example.androidconcepts.hilt.room

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidconcepts.databinding.ItemAnimalRowBinding

class AnimalAdapter(
    private val onDelete: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalAdapter.ViewHolder>() {

    private var animals: List<Animal> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newAnimals: List<Animal>) {
        animals = newAnimals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnimalRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(animals[position])
    }

    override fun getItemCount(): Int = animals.size

    inner class ViewHolder(private val binding: ItemAnimalRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(animal: Animal) {
            binding.tvAnimalName.text = animal.name
            binding.tvAnimalSpecies.text = animal.species
            binding.btnDeleteAnimal.setOnClickListener { onDelete(animal) }
        }
    }
}
