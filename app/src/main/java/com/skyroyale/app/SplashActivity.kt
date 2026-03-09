package com.skyroyale.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tvLogo = findViewById<TextView>(R.id.tvLogo)
        val tvAppName = findViewById<TextView>(R.id.tvAppName)

        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 700
            fillAfter = true
        }
        tvLogo.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            val nameFade = AlphaAnimation(0f, 1f).apply {
                duration = 500
                fillAfter = true
            }
            tvAppName.startAnimation(nameFade)
        }, 300)

        Handler(Looper.getMainLooper()).postDelayed({
            val accountManager = AccountManager(this)
            val destination = if (accountManager.isLoggedIn()) {
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }
            startActivity(Intent(this, destination))
            finish()
        }, 2000)
    }
}
