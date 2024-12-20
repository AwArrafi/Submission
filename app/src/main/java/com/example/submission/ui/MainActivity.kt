package com.example.submission.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission.R
import com.example.submission.adapter.StoryAdapter
import com.example.submission.data.DataStoreManager
import com.example.submission.databinding.ActivityMainBinding
import com.example.submission.di.Injection
import com.example.submission.viewmodel.StoryViewModel
import com.example.submission.viewmodel.StoryViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var storyViewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize DataStoreManager with application context
        dataStoreManager = DataStoreManager(applicationContext)

        // Setup RecyclerView
        val storyAdapter = StoryAdapter(emptyList()) { story ->
            story.id?.let { id ->
                openStoryDetail(id)
            } ?: run {
                Toast.makeText(this, "ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = storyAdapter

        // Set up logout button
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.clearToken()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // Check if token exists and then get stories
        lifecycleScope.launch {
            dataStoreManager.token.collect { token ->
                if (token.isNullOrEmpty()) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val storyRepository = Injection.provideStoryRepository(applicationContext)
                    storyViewModel = ViewModelProvider(this@MainActivity, StoryViewModelFactory(storyRepository)).get(StoryViewModel::class.java)
                    storyViewModel.getStories()
                    storyViewModel.stories.observe(this@MainActivity) { storyResponse ->
                        val storiesList = storyResponse?.listStory ?: emptyList()
                        storyAdapter.updateData(storiesList)
                    }
                }
            }
        }

        // FAB - Menambahkan Cerita Baru
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openStoryDetail(storyId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("STORY_ID", storyId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (::storyViewModel.isInitialized) {
            storyViewModel.getStories()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}