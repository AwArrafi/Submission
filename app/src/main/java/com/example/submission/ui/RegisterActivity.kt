package com.example.submission.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.submission.Resource
import com.example.submission.databinding.ActivityRegisterBinding
import com.example.submission.di.Injection
import com.example.submission.viewmodel.AuthViewModel
import com.example.submission.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(Injection.provideAuthRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply fade-in animation for UI elements
        applyFadeInAnimation()

        // Handle email validation
        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.emailLayout.error = "Email tidak valid"
                } else {
                    binding.emailLayout.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Handle name validation
        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()
                if (name.isEmpty()) {
                    binding.nameLayout.error = "Nama tidak boleh kosong"
                } else {
                    binding.nameLayout.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        lifecycleScope.launch {
            registerViewModel.registerState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // No progress bar anymore
                    }
                    is Resource.Success -> {
                        Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@RegisterActivity, resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Register button action
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            // Validasi input
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password.length >= 8) {
                // Memanggil fungsi register dari AuthViewModel
                registerViewModel.register(name, email, password)
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Apply fade-in animation to the UI components
    private fun applyFadeInAnimation() {
        val fadeInName = ObjectAnimator.ofFloat(binding.edRegisterName, "alpha", 0f, 1f)
        val fadeInEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, "alpha", 0f, 1f)
        val fadeInPassword = ObjectAnimator.ofFloat(binding.edRegisterPassword, "alpha", 0f, 1f)
        val fadeInButton = ObjectAnimator.ofFloat(binding.btnRegister, "alpha", 0f, 1f)

        fadeInName.duration = 1500
        fadeInEmail.duration = 1500
        fadeInPassword.duration = 1500
        fadeInButton.duration = 1500

        AnimatorSet().apply {
            playTogether(fadeInName, fadeInEmail, fadeInPassword, fadeInButton)
            start()
        }
    }
}
