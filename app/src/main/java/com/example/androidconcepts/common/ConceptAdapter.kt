package com.example.androidconcepts.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidconcepts.databinding.ItemConceptBinding

class ConceptAdapter(private val concepts : List<CONCEPTS>, private val onOptionClicked : (Int) -> Unit) : RecyclerView.Adapter<ConceptAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemConceptBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(concepts[position])
    }

    override fun getItemCount(): Int {
        return concepts.size
    }

    inner class ProjectViewHolder(private val binding: ItemConceptBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : CONCEPTS) = with(binding) {
            buttonConcept.text = root.context.getText(item.topicNameResId)
            buttonConcept.setOnClickListener {
                onOptionClicked(item.topicId)
            }
        }
    }
}