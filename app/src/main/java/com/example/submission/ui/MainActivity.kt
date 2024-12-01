package com.example.submission.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
                openStoryDetail(id) // Kirimkan id yang valid ke DetailActivity
            } ?: run {
                // Jika id null, beri tahu pengguna atau lakukan tindakan lain
                Toast.makeText(this, "ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = storyAdapter

        // Set up logout button
        binding.btnLogout.setOnClickListener {
            android.util.Log.d("MainActivity", "Logout button clicked")

            // Clear the token from DataStore
            lifecycleScope.launch {
                dataStoreManager.clearToken()  // Clear token from DataStore
                android.util.Log.d("MainActivity", "Token cleared from DataStore")

                // After clearing token, navigate back to LoginActivity
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()  // Close MainActivity so the user can't go back to it
                Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // Check if token exists and then get stories
        lifecycleScope.launch {
            dataStoreManager.token.collect { token ->
                if (token.isNullOrEmpty()) {
                    // Jika token kosong, arahkan ke LoginActivity
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()  // Tutup MainActivity agar pengguna tidak bisa kembali
                } else {
                    // Jika token ada, teruskan dengan mengambil stories
                    val storyRepository = Injection.provideStoryRepository(applicationContext)
                    storyViewModel = ViewModelProvider(this@MainActivity, StoryViewModelFactory(storyRepository)).get(StoryViewModel::class.java)

                    // Ambil cerita tanpa parameter
                    storyViewModel.getStories()

                    // Observasi perubahan data stories
                    storyViewModel.stories.observe(this@MainActivity, { storyResponse ->
                        val storiesList = storyResponse?.listStory ?: emptyList()
                        storyAdapter.updateData(storiesList)  // Update adapter dengan data cerita
                    })

                    android.util.Log.d("MainActivity", "Token ditemukan, melanjutkan MainActivity")
                }
            }
        }

        // FAB - Menambahkan Cerita Baru
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    // Fungsi untuk membuka DetailActivity dengan mengirimkan ID cerita
    private fun openStoryDetail(storyId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("STORY_ID", storyId) // Mengirimkan ID cerita ke DetailActivity
        startActivity(intent)
    }
}
