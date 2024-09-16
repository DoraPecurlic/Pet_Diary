package com.example.petdiary.utilities

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object AuthHelper {
    private const val prefsName = "com.example.pet_diary.prefs"
    private const val emailKey = "email"
    private const val passwordKey = "password"
    private const val rememberMeKey = "remember_me"

    fun saveCredentials(context: Context, email: String, password: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(emailKey, email)
            putString(passwordKey, password)
            apply()
        }
    }
    fun setRememberMe(context: Context, rememberMe: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(rememberMeKey, rememberMe)
            apply()
        }
    }

    fun loadCredentials(context: Context): Pair<String, String> {
        val prefs: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val email = prefs.getString(emailKey, "") ?: ""
        val password = prefs.getString(passwordKey, "") ?: ""
        return Pair(email, password)
    }

    fun isRememberMeChecked(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return prefs.getBoolean(rememberMeKey, false)
    }

    fun clearRememberMe(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            remove(emailKey)
            remove(passwordKey)
            remove(rememberMeKey)
            apply()
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult? {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            null
        }
    }
}
