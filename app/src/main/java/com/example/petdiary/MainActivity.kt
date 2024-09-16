package com.example.petdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.petdiary.repository.StorageRepository
import com.example.petdiary.navigation.Main
import com.example.petdiary.view.LoginScreen
import com.example.petdiary.ui.theme.PetDiaryTheme
import com.example.petdiary.view.RegisterScreen

import com.example.petdiary.view.WelcomeScreen
import com.example.petdiary.viewModel.LoginViewModel
import com.example.petdiary.viewModel.NotesViewModel
import com.example.petdiary.viewModel.RegisterViewModel
import com.example.petdiary.viewModel.RemindersViewModel
import com.example.petdiary.viewModel.RemindersViewModelFactory


class MainActivity : ComponentActivity() {

    private val notesViewModel: NotesViewModel by viewModels()


    val repository = StorageRepository()
    val remindersViewModel: RemindersViewModel by viewModels {
        RemindersViewModelFactory(application, repository)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetDiaryTheme {
                NavigationView(notesViewModel = notesViewModel, remindersViewModel = remindersViewModel )
            }
        }
    }

}


@Composable
fun NavigationView(notesViewModel: NotesViewModel, remindersViewModel: RemindersViewModel ) {

    val navController = rememberNavController()
    val context = LocalContext.current


    NavHost(navController = navController, startDestination = "welcome" ){

        composable("welcome"){ WelcomeScreen(navController) }
        composable("login"){ LoginScreen(navController, context, loginViewModel = LoginViewModel()) }
        composable("signup"){ RegisterScreen(navController, context, registerViewModel = RegisterViewModel()) }
        composable("main"){ Main(notesViewModel = notesViewModel, remindersViewModel = remindersViewModel )}

    }
}


