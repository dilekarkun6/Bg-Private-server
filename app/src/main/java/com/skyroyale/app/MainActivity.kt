package com.skyroyale.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Random

class MainActivity : AppCompatActivity() {

    private lateinit var tabGames: TextView
    private lateinit var tabProfile: TextView
    private lateinit var contentContainer: FrameLayout
    private lateinit var tvServerStatus: TextView
    private lateinit var tvOnlineCount: TextView
    private lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accountManager = AccountManager(this)

        // Giriş yapılmamışsa login ekranına gönder
        if (!accountManager.isLoggedIn()) {
            goToLogin()
            return
        }

        tabGames = findViewById(R.id.tabGames)
        tabProfile = findViewById(R.id.tabProfile)
        contentContainer = findViewById(R.id.contentContainer)
        tvServerStatus = findViewById(R.id.tvServerStatus)
        tvOnlineCount = findViewById(R.id.tvOnlineCount)

        tabGames.setOnClickListener { switchTab(0) }
        tabProfile.setOnClickListener { switchTab(1) }

        switchTab(0)
        simulateServerConnection()
    }

    private fun switchTab(tab: Int) {
        contentContainer.removeAllViews()
        val user = accountManager.getCurrentUser()

        if (tab == 0) {
            tabGames.setTextColor(ContextCompat.getColor(this, R.color.accent_cyan))
            tabProfile.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))

            val view = layoutInflater.inflate(R.layout.fragment_games, contentContainer, false)
            view.findViewById<TextView>(R.id.btnPlaySkyRoyale)?.setOnClickListener {
                Toast.makeText(this, "🎮 Sky Royale'e bağlanılıyor...", Toast.LENGTH_SHORT).show()
            }
            contentContainer.addView(view)

        } else {
            tabProfile.setTextColor(ContextCompat.getColor(this, R.color.accent_cyan))
            tabGames.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))

            val view = layoutInflater.inflate(R.layout.fragment_profile, contentContainer, false)

            // Kullanıcı bilgilerini doldur
            user?.let {
                view.findViewById<TextView>(R.id.tvUsername)?.text = it.username
                view.findViewById<TextView>(R.id.tvLevel)?.text = "SEVİYE ${it.level}"
                view.findViewById<TextView>(R.id.tvWins)?.text = it.wins.toString()
                view.findViewById<TextView>(R.id.tvKills)?.text = it.kills.toString()
                view.findViewById<TextView>(R.id.tvGames)?.text = it.games.toString()
                val wr = if (it.games > 0) "${(it.wins * 100 / it.games)}%" else "0%"
                view.findViewById<TextView>(R.id.tvWinRate)?.text = wr
            }

            // Çıkış butonu
            view.findViewById<View>(R.id.btnLogout)?.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Çıkış Yap")
                    .setMessage("Hesabından çıkmak istediğine emin misin?")
                    .setPositiveButton("Çıkış") { _, _ ->
                        accountManager.logout()
                        goToLogin()
                    }
                    .setNegativeButton("İptal", null)
                    .show()
            }

            contentContainer.addView(view)
        }
    }

    private fun simulateServerConnection() {
        tvServerStatus.text = "Bağlanıyor..."
        tvOnlineCount.text = "..."

        Handler(Looper.getMainLooper()).postDelayed({
            tvServerStatus.text = "● Çevrimiçi"
            tvServerStatus.setTextColor(ContextCompat.getColor(this, R.color.online_dot))
            val fakeOnline = 12 + Random().nextInt(88)
            tvOnlineCount.text = fakeOnline.toString()

            val skyRoyalePlayers = contentContainer.findViewById<TextView>(R.id.tvSkyRoyalePlayers)
            skyRoyalePlayers?.text = "${4 + Random().nextInt(20)} oyuncu"
        }, 1500)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
