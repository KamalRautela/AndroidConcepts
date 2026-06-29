package com.example.androidconcepts.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidconcepts.databinding.ItemConceptBinding

class TopicAdapter(private val topics : List<TOPICS>, private val onOptionClicked : (Int) -> Unit) : RecyclerView.Adapter<TopicAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemConceptBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(topics[position])
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    inner class ProjectViewHolder(private val binding: ItemConceptBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : TOPICS) = with(binding) {
            buttonConcept.text = root.context.getText(item.topicNameResId)
            buttonConcept.setOnClickListener {
                onOptionClicked(item.topicId)
            }
        }
    }
}