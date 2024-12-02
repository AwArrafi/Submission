package com.example.submission.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.submission.api.ApiConfig
import com.example.submission.data.DataStoreManager
import com.example.submission.databinding.ActivityAddStoryBinding
import com.example.submission.utils.getImageUri
import com.example.submission.viewmodel.AddStoryViewModel
import com.example.submission.viewmodel.AddStoryViewModelFactory

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null

    // Initialize ViewModel with ViewModelFactory
    private val addStoryViewModel: AddStoryViewModel by lazy {
        val dataStoreManager = DataStoreManager(applicationContext)  // Pastikan context aplikasi diteruskan
        val factory = AddStoryViewModelFactory(
            application,  // Pass Application instance
            ApiConfig.api, // Pass the ApiService instance from ApiConfig (tanpa token)
            dataStoreManager // Pass the DataStoreManager instance
        )
        ViewModelProvider(this, factory)[AddStoryViewModel::class.java]
    }

    // ActivityResultLauncher untuk mengambil gambar dari kamera
    private val launcherIntentCamera: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                showImage()
            } else {
                currentImageUri = null
                Toast.makeText(this, "Camera failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

    // Register ActivityResultLauncher untuk memilih gambar
    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.previewImageView.setImageURI(uri)
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set listeners
        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.uploadButton.setOnClickListener {
            val description = binding.edAddDescription.text.toString()
            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
            } else {
                uploadStory(description, currentImageUri)
            }
        }

        // Observe loading and error states from ViewModel
        addStoryViewModel.isLoading.observe(this) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        addStoryViewModel.errorMessage.observe(this) { error ->
            showToast(error)
        }

        addStoryViewModel.uploadResponse.observe(this) { response ->
            if (response?.isSuccessful == true) {
                Toast.makeText(this, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to upload story", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGallery() {
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        launcherGallery.launch(pickVisualMediaRequest)
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let {
            launcherIntentCamera.launch(it)
        }
    }

    private fun showImage() {
        binding.previewImageView.setImageURI(currentImageUri)
    }

    private fun uploadStory(description: String, imageUri: Uri?) {
        imageUri?.let {
            addStoryViewModel.postStory(description, it, null, null) // Without location
        } ?: showToast("Please select an image")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
