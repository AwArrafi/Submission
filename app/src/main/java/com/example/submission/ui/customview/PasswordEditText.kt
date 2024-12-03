package com.example.submission.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.submission.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var warningIcon: Drawable? = null
    private var isPasswordVisible = false

    init {
        // Inisialisasi drawable untuk ikon peringatan
        warningIcon = ContextCompat.getDrawable(context, R.drawable.ic_warning)

        // Listener untuk validasi panjang password
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Cek apakah password lebih dari 8 karakter setelah pengguna mulai mengetik
                if (s != null && s.isNotEmpty()) {
                    if (s.length < 8) {
                        showWarningIcon()
                        error = "Password harus lebih dari 8 karakter"
                    } else {
                        hideWarningIcon()
                        error = null  // Menghapus pesan error jika password valid
                    }
                } else {
                    hideWarningIcon() // Sembunyikan warning icon jika input kosong
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set transformation untuk password yang disembunyikan
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    // Override onDraw untuk menampilkan hint
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Password"  // Menampilkan hint secara manual
    }

    // Menampilkan ikon peringatan jika password terlalu pendek
    private fun showWarningIcon() {
        warningIcon?.let {
            setCompoundDrawablesWithIntrinsicBounds(null, null, it, null)
        }
    }

    // Menghilangkan ikon peringatan jika password valid
    private fun hideWarningIcon() {
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }

    // Menambahkan fitur untuk menampilkan/menyembunyikan password
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && event.action == MotionEvent.ACTION_UP) {
            val drawableRight = compoundDrawables[2]
            if (drawableRight != null && event.x >= (width - paddingRight - drawableRight.intrinsicWidth)) {
                togglePasswordVisibility()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // Fungsi untuk toggle visibility password
    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        transformationMethod = if (isPasswordVisible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        // Reset cursor ke posisi semula setelah toggle
        setSelection(text?.length ?: 0)
    }
}
