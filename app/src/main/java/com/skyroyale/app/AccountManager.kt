package com.skyroyale.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * Hesap yöneticisi.
 * Şu an: Telefon hafızasında saklar (SharedPreferences)
 * İleride: Sunucuya bağlanacak (Firebase/Custom API)
 */
class AccountManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("nxgo_accounts", Context.MODE_PRIVATE)
    private val sessionPrefs: SharedPreferences = context.getSharedPreferences("nxgo_session", Context.MODE_PRIVATE)

    // ─── Kayıt ───────────────────────────────────────────────
    fun register(username: String, email: String, password: String): Result {
        if (username.length < 3) return Result.Error("Kullanıcı adı en az 3 karakter olmalı")
        if (password.length < 6) return Result.Error("Şifre en az 6 karakter olmalı")
        if (!email.contains("@")) return Result.Error("Geçerli bir e-posta gir")

        // Kullanıcı adı dolu mu?
        if (prefs.contains("user_$username")) {
            return Result.Error("Bu kullanıcı adı zaten alınmış")
        }

        // Hesabı kaydet
        val userJson = JSONObject().apply {
            put("username", username)
            put("email", email)
            put("password", hashPassword(password))
            put("createdAt", System.currentTimeMillis())
            put("level", 1)
            put("wins", 0)
            put("kills", 0)
            put("games", 0)
        }
        prefs.edit().putString("user_$username", userJson.toString()).apply()

        // Otomatik giriş yap
        saveSession(username)
        return Result.Success(username)
    }

    // ─── Giriş ───────────────────────────────────────────────
    fun login(username: String, password: String): Result {
        val userJson = prefs.getString("user_$username", null)
            ?: return Result.Error("Kullanıcı bulunamadı")

        val user = JSONObject(userJson)
        if (user.getString("password") != hashPassword(password)) {
            return Result.Error("Şifre yanlış")
        }

        saveSession(username)
        return Result.Success(username)
    }

    // ─── Oturum ──────────────────────────────────────────────
    fun isLoggedIn(): Boolean = sessionPrefs.getString("current_user", null) != null

    fun getCurrentUser(): UserData? {
        val username = sessionPrefs.getString("current_user", null) ?: return null
        val userJson = prefs.getString("user_$username", null) ?: return null
        return parseUser(userJson)
    }

    fun logout() {
        sessionPrefs.edit().remove("current_user").apply()
    }

    private fun saveSession(username: String) {
        sessionPrefs.edit().putString("current_user", username).apply()
    }

    // ─── Basit hash (gerçek sunucu olmadan) ──────────────────
    private fun hashPassword(password: String): String {
        var hash = 5381L
        for (c in password) hash = hash * 33 + c.code
        return hash.toString(16)
    }

    private fun parseUser(json: String): UserData {
        val obj = JSONObject(json)
        return UserData(
            username = obj.getString("username"),
            email = obj.getString("email"),
            level = obj.getInt("level"),
            wins = obj.getInt("wins"),
            kills = obj.getInt("kills"),
            games = obj.getInt("games")
        )
    }

    // ─── Data sınıfları ──────────────────────────────────────
    data class UserData(
        val username: String,
        val email: String,
        val level: Int,
        val wins: Int,
        val kills: Int,
        val games: Int
    )

    sealed class Result {
        data class Success(val username: String) : Result()
        data class Error(val message: String) : Result()
    }
}
