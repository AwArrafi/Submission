package com.example.submission.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.submission.Resource
import com.example.submission.data.DataStoreManager
import com.example.submission.databinding.ActivityLoginBinding
import com.example.submission.di.Injection
import com.example.submission.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: AuthViewModel
    private val dataStoreManager = DataStoreManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pastikan CircularProgressIndicator disembunyikan saat aktivitas pertama kali dibuka
        binding.progressBarLogin.visibility = android.view.View.GONE

        // Cek token di DataStore sebelum lanjut ke login
        lifecycleScope.launch {
            val token = dataStoreManager.token.collect { token ->
                if (token != null && token.isNotEmpty()) {
                    // Jika token ada, langsung arahkan ke MainActivity
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish() // Tutup LoginActivity agar pengguna tidak bisa kembali
                }
            }
        }

        // Mendapatkan AuthViewModel melalui Injection
        loginViewModel = Injection.provideAuthViewModel()

        // Mengamati status login menggunakan collect di lifecycleScope
        lifecycleScope.launch {
            loginViewModel.loginState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Tampilkan ProgressBar saat login dimulai
                        binding.progressBarLogin.visibility = android.view.View.VISIBLE
                    }
                    is Resource.Success -> {
                        // Sembunyikan ProgressBar setelah login berhasil
                        binding.progressBarLogin.visibility = android.view.View.GONE

                        val loginResult = resource.data?.loginResult

                        if (loginResult != null && loginResult.token != null) {
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                            // Simpan token ke DataStore setelah login berhasil
                            val token = loginResult.token
                            lifecycleScope.launch {
                                dataStoreManager.saveToken(token)  // Simpan token di DataStore
                            }

                            // Lanjutkan ke halaman utama setelah login berhasil
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()  // Tutup LoginActivity supaya pengguna tidak bisa kembali
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed, token not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        // Sembunyikan ProgressBar jika terjadi error
                        binding.progressBarLogin.visibility = android.view.View.GONE
                        Toast.makeText(this@LoginActivity, resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Handle Login Button Click
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.progressBarLogin.visibility = android.view.View.VISIBLE
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Pindah ke RegisterActivity jika belum punya akun
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
