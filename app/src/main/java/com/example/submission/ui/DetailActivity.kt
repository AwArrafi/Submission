package com.example.submission.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.submission.databinding.ActivityDetailBinding
import com.example.submission.di.Injection
import com.example.submission.viewmodel.StoryViewModel
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyId: String
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil Story ID dari Intent
        storyId = intent.getStringExtra("STORY_ID") ?: ""  // Ambil id yang dikirim dari MainActivity

        if (storyId.isNotEmpty()) {
            // Jalankan coroutine untuk memanggil provideStoryViewModel
            lifecycleScope.launch {
                storyViewModel = Injection.provideStoryViewModel(applicationContext)

                // Observasi perubahan data detail cerita
                storyViewModel.storyDetail.observe(this@DetailActivity, Observer { storyDetailResponse ->
                    if (storyDetailResponse.error == false) {
                        val story = storyDetailResponse.story
                        binding.tvDetailName.text = story?.name
                        binding.tvDetailDescription.text = story?.description
                        Glide.with(this@DetailActivity)
                            .load(story?.photoUrl)
                            .into(binding.ivDetailPhoto)
                    } else {
                        Toast.makeText(this@DetailActivity, "Error: ${storyDetailResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                })

                // Ambil detail cerita berdasarkan ID
                storyViewModel.getStoryDetail(storyId)
            }
        } else {
            Toast.makeText(this, "ID cerita tidak valid!", Toast.LENGTH_SHORT).show()
        }
    }
}
