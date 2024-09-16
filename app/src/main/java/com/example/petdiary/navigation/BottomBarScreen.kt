package com.example.petdiary.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    data object NotesScreen: BottomBarScreen(
        route = "notesScreen",
        title = "Notes",
        icon = Icons.Default.Create
    )
    data object RemindersScreen: BottomBarScreen(
        route = "remindersScreen",
        title = "Reminders",
        icon = Icons.Default.DateRange
    )



}