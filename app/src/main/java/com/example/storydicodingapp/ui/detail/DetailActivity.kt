package com.example.storydicodingapp.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storydicodingapp.data.models.ListStoryItem
import com.example.storydicodingapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ListStoryItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        item = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_DETAIL)!!
        } else {
            intent.getParcelableExtra(KEY_DETAIL, ListStoryItem::class.java)!!
        }

        setView()
    }

    private fun setView() {
        with(binding) {
            toolbar.setNavigationOnClickListener { finish() }
            Glide.with(root)
                .load(item.photoUrl)
                .circleCrop()
                .into(binding.ivStory)

            tvUsername.text = item.name
            tvDesc.text = item.description
        }
    }

    companion object {
        const val KEY_DETAIL = "key_detail"
    }
}