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
import com.example.submission.adapter.StoryPagingAdapter
import com.example.submission.data.DataStoreManager
import com.example.submission.databinding.ActivityMainBinding
import com.example.submission.di.Injection
import com.example.submission.viewmodel.StoryViewModel
import com.example.submission.viewmodel.StoryViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var storyPagingAdapter: StoryPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize DataStoreManager
        dataStoreManager = DataStoreManager(applicationContext)

        // Initialize PagingAdapter and RecyclerView
        storyPagingAdapter = StoryPagingAdapter()
        storyAdapter = StoryAdapter(emptyList()) { story ->
            story.id?.let { id ->
                openStoryDetail(id) // Open detail if ID is valid
            } ?: run {
                Toast.makeText(this, "ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvStories.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            dataStoreManager.token.collect { token ->
                if (token.isNullOrEmpty()) {
                    // Redirect to LoginActivity if token is null
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val storyRepository = Injection.provideStoryRepository(applicationContext)
                    storyViewModel = ViewModelProvider(
                        this@MainActivity,
                        StoryViewModelFactory(storyRepository)
                    )[StoryViewModel::class.java]

                    setupStoryList(token) // Call setup function for story display
                }
            }
        }

        // Set up FAB for adding new story
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        // Set up Logout button
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.clearToken()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupStoryList(token: String) {
        // Observe data using Paging or standard list based on requirement
        lifecycleScope.launch {
            storyViewModel.getStoriesWithPaging().collectLatest { pagingData ->
                binding.rvStories.adapter = storyPagingAdapter
                storyPagingAdapter.submitData(pagingData)
            }
        }

        // Optional: Observing non-paging data
        storyViewModel.stories.observe(this) { storyResponse ->
            val storiesList = storyResponse?.listStory ?: emptyList()
            if (storiesList.isNotEmpty()) {
                binding.rvStories.adapter = storyAdapter
                storyAdapter.updateData(storiesList)
            }
        }
    }

    private fun openStoryDetail(storyId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("STORY_ID", storyId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Update stories when returning to MainActivity
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
