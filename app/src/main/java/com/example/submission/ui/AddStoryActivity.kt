package com.example.submission.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.submission.databinding.ActivityAddStoryBinding
import com.example.submission.utils.getImageUri

class AddStoryActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null

    // ActivityResultLauncher untuk mengambil gambar dari kamera
    private val launcherIntentCamera: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                // Jika pengambilan foto berhasil, tampilkan gambar
                showImage()
            } else {
                // Jika gagal, reset URI
                currentImageUri = null
                Toast.makeText(this, "Camera failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
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
            // Upload logic, gunakan currentImageUri dan deskripsi dari EditText
            val description = binding.edAddDescription.text.toString()
            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
            } else {
                uploadStory(description, currentImageUri)
            }
        }
    }

    // Fungsi untuk membuka Photo Picker (Gallery)
    private fun startGallery() {
        // Membuat request untuk memilih gambar saja
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        launcherGallery.launch(pickVisualMediaRequest) // Menggunakan request ini untuk meluncurkan Photo Picker
    }

    // Register ActivityResultLauncher untuk memilih gambar
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Gambar dipilih, update ImageView dan simpan URI
            currentImageUri = uri
            binding.previewImageView.setImageURI(uri) // Update ImageView
        } else {
            // Tidak ada gambar yang dipilih
            Log.d("Photo Picker", "No media selected")
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk memulai kamera dan mengambil foto
    private fun startCamera() {
        // Dapatkan URI tempat foto akan disimpan
        currentImageUri = getImageUri(this)

        // Pastikan URI tidak null, lalu jalankan kamera
        currentImageUri?.let {
            launcherIntentCamera.launch(it)
        }
    }

    // Fungsi untuk menampilkan gambar yang diambil ke ImageView
    private fun showImage() {
        binding.previewImageView.setImageURI(currentImageUri)
    }

    // Fungsi untuk upload cerita (sesuaikan dengan backend Anda)
    private fun uploadStory(description: String, imageUri: Uri?) {
        // Upload logic di sini, kirim deskripsi dan imageUri ke API
        // Misalnya menggunakan Retrofit atau Volley untuk upload gambar dan deskripsi ke server
        Log.d("Upload Story", "Description: $description, Image URI: $imageUri")
    }
}
