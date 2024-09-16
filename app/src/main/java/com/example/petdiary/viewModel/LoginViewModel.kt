package com.example.petdiary.viewModel

import androidx.lifecycle.ViewModel
import android.content.Context
import android.widget.Toast

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.petdiary.utilities.AuthHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    fun signIn(context: Context, email: String, password: String, rememberMe: Boolean, navController: NavController) {
        viewModelScope.launch {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (rememberMe) {
                                AuthHelper.saveCredentials(context, email, password)
                                AuthHelper.setRememberMe(context, true)
                            }
                            Toast.makeText(context, "Logged in successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate("main")
                        } else {
                            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}