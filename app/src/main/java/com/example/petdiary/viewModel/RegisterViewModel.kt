package com.example.petdiary.viewModel
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.petdiary.utilities.AuthHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    fun register(context: Context, email: String, password: String, rememberMe: Boolean, navController: NavController) {
        viewModelScope.launch {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { signInTask ->
                                    if (signInTask.isSuccessful) {
                                        val user = auth.currentUser
                                        user?.let { currentUser ->
                                            val userID = currentUser.uid
                                            val userData = hashMapOf(
                                                "email" to email
                                            )
                                            db.collection("users")
                                                .document(userID)
                                                .set(userData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Registration and login successful", Toast.LENGTH_SHORT).show()
                                                    if (rememberMe) {
                                                        AuthHelper.saveCredentials(context, email, password)
                                                        AuthHelper.setRememberMe(context, true)
                                                    }
                                                    navController.navigate("main")
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(context, "Failed to add user data", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    } else {
                                        Toast.makeText(context, "Login after registration failed", Toast.LENGTH_SHORT).show()
                                        navController.navigate("login_screen")
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
