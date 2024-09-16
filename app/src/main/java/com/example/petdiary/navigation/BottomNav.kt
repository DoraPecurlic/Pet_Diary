import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.petdiary.navigation.BottomBarScreen
import com.example.petdiary.repository.StorageRepository
import com.example.petdiary.view.LoginScreen
import com.example.petdiary.view.AddReminderScreen
import com.example.petdiary.view.DetailScreen
import com.example.petdiary.view.NotesScreen
import com.example.petdiary.view.RemindersScreen
import com.example.petdiary.view.WelcomeScreen
import com.example.petdiary.viewModel.DetailReminderViewModel
import com.example.petdiary.viewModel.DetailReminderViewModelFactory
import com.example.petdiary.viewModel.DetailViewModel
import com.example.petdiary.viewModel.DetailViewModelFactory
import com.example.petdiary.viewModel.LoginViewModel
import com.example.petdiary.viewModel.NotesViewModel
import com.example.petdiary.viewModel.RemindersViewModel

@Composable
fun BottomNav(
    navController: NavHostController,
    notesViewModel: NotesViewModel,
    remindersViewModel: RemindersViewModel
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.NotesScreen.route
    ) {
        composable(route = BottomBarScreen.NotesScreen.route) {
            NotesScreen(
                notesViewModel = notesViewModel,
                onNoteClick = { /* Handle note click */ },
                navToDetailPage = { navController.navigate("detail_screen/new") },
                navToLoginPage = { navController.navigate("welcome") },
                navController = navController
            )
        }

        composable(route = "login_screen") {
            LoginScreen(navController = navController, context = context, loginViewModel = LoginViewModel())
        }
        composable(route = "welcome") {
            WelcomeScreen(navController = navController)
        }

        composable(route = BottomBarScreen.RemindersScreen.route) {
            RemindersScreen(
                remindersViewModel = remindersViewModel,
                onReminderClick = { /* Handle reminder click */ },
                navToDetailReminderPage = { navController.navigate("add_reminder/new") },
                navToLoginPage = { navController.navigate("login_screen") },
                navController = navController
            )
        }

        composable(
            route = "add_reminder/{reminderId}",
            arguments = listOf(navArgument("reminderId") { defaultValue = "" })
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getString("reminderId") ?: ""
            val repository = StorageRepository()
            val detailReminderViewModel: DetailReminderViewModel = viewModel(
                factory = DetailReminderViewModelFactory(application, repository)
            )
            AddReminderScreen(
                detailReminderViewModel = detailReminderViewModel,
                reminderId = reminderId,
                navController = navController,
                onNavigate = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack() // Vraćanje nazad bez spremanja
                }
            )
        }

        composable(
            route = "detail_screen/{noteId}",
            arguments = listOf(navArgument("noteId") { defaultValue = "" })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val repository = StorageRepository()
            val detailViewModel: DetailViewModel = viewModel(
                factory = DetailViewModelFactory(repository)
            )
            DetailScreen(
                detailViewModel = detailViewModel,
                noteId = noteId,
                onNavigate = {
                    navController.popBackStack()
                }
            )
        }
    }
}