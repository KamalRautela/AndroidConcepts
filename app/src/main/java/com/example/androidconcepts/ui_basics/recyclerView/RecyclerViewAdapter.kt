package com.example.androidconcepts.ui_basics.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidconcepts.common.UiBASICS
import com.example.androidconcepts.databinding.ItemTopicBinding

class RecyclerViewAdapter(private val topic: List<UiBASICS>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        p1: Int
    ): ViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(topic[position])
    }

    override fun getItemCount(): Int = topic.size

    class ViewHolder(private val binding : ItemTopicBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic : UiBASICS) {
            binding.tvTopicNumber.text = topic.topicId.toString()
            binding.tvTopicName.text = binding.root.context.getString(topic.topicNameResId)
            binding.tvTopicDesc.text = ""
        }
    }
}