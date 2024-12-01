package com.example.submission.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission.databinding.ItemStoryBinding
import com.example.submission.response.ListStoryItem

class StoryAdapter(
    private var stories: List<ListStoryItem>,
    private val onItemClick: (ListStoryItem) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = stories.size

    inner class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            // Bind name and photo to views
            binding.tvItemName.text = story.name

            // Load image using Glide
            Glide.with(binding.ivItemPhoto.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            // Handle click event for item
            itemView.setOnClickListener { onItemClick(story) }
        }
    }

    // Update stories data
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newStories: List<ListStoryItem>) {
        stories = newStories
        notifyDataSetChanged()
    }
}
