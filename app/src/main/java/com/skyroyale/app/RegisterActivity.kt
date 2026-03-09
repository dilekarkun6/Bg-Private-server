package com.skyroyale.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        accountManager = AccountManager(this)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordConfirm = findViewById<EditText>(R.id.etPasswordConfirm)
        val btnRegister = findViewById<TextView>(R.id.btnRegister)
        val btnGoLogin = findViewById<TextView>(R.id.btnGoLogin)
        val btnBack = findViewById<TextView>(R.id.btnBack)
        val tvError = findViewById<TextView>(R.id.tvError)

        btnBack.setOnClickListener { finish() }
        btnGoLogin.setOnClickListener { finish() }

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val passwordConfirm = etPasswordConfirm.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError(tvError, "Tüm alanları doldur")
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                showError(tvError, "Şifreler eşleşmiyor")
                return@setOnClickListener
            }

            when (val result = accountManager.register(username, email, password)) {
                is AccountManager.Result.Success -> {
                    Toast.makeText(this, "✅ Hoş geldin, ${result.username}!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is AccountManager.Result.Error -> showError(tvError, result.message)
            }
        }
    }

    private fun showError(tv: TextView, msg: String) {
        tv.text = msg
        tv.visibility = View.VISIBLE
    }
}
