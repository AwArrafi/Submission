package com.example.submission.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.submission.R
import com.example.submission.data.DataStoreManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi DataStoreManager dengan aplikasi context
        dataStoreManager = DataStoreManager(applicationContext)

        // Periksa apakah token ada
        lifecycleScope.launch {
            dataStoreManager.token.collect { token ->
                if (token.isNullOrEmpty()) {
                    // Token tidak ada, arahkan ke LoginActivity
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()  // Tutup MainActivity supaya tidak kembali ke sini
                } else {
                    // Token ada, lanjutkan dengan proses utama aplikasi
                    // Misalnya: tampilkan data cerita atau halaman utama
                    setContentView(R.layout.activity_main)  // Atau tampilkan UI MainActivity
                }
            }
        }
    }
}
