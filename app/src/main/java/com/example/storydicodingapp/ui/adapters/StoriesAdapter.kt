package com.example.storydicodingapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storydicodingapp.data.models.ListStoryItem
import com.example.storydicodingapp.databinding.ItemStoriesRowBinding

class StoriesAdapter : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    private val storyList: ArrayList<ListStoryItem> = arrayListOf()
    var onStoryClick: ((ListStoryItem, View) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(storyList: ArrayList<ListStoryItem>) {
        this.storyList.clear()
        this.storyList.addAll(storyList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStoriesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(storyList[position])
    }

    override fun getItemCount() = storyList.size

    inner class ViewHolder(private var binding: ItemStoriesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            binding.apply {
                Glide.with(root)
                    .load(item.photoUrl)
                    .circleCrop()
                    .into(binding.ivStory)

                tvUsername.text = item.name
                tvDesc.text = item.description

                root.setOnClickListener {
                    onStoryClick?.invoke(item, root)
                }
            }
        }
    }
}