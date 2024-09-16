package com.example.petdiary.view


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.petdiary.viewModel.DetailReminderViewModel
import com.google.firebase.Timestamp
import java.util.*
import java.text.SimpleDateFormat
import androidx.navigation.NavController
import com.example.petdiary.viewModel.RemindersUIState
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AddReminderScreen(
    detailReminderViewModel: DetailReminderViewModel?,
    reminderId: String,
    navController: NavController,
    onNavigate: () -> Unit,
    onCancel: () -> Unit
) {
    val detailUIState = detailReminderViewModel?.reminderUIState ?: RemindersUIState()


    var reminderTime by remember { mutableStateOf(detailUIState.reminderTime) }
    val calendar = Calendar.getInstance()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val isNewReminder = reminderId == "new"
    val isReminderIdNotBlank = reminderId.isNotBlank()

    LaunchedEffect(key1 = Unit) {
        if (isNewReminder) {
            detailReminderViewModel?.resetState()
        }else if (isReminderIdNotBlank){
            detailReminderViewModel?.getReminder(reminderId)

        }
    }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color(0xFF253334),
                title = { Text("Add New Reminder", color = Color.White, fontSize = 20.sp) },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (detailUIState.reminderAddedStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("Added Note Successfully")
                    detailReminderViewModel?.resetReminderAddedStatus()
                    onNavigate.invoke()
                }
            }

            if (detailUIState.updatedReminderStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("Note Updated Successfully")
                    detailReminderViewModel?.resetReminderAddedStatus()
                    onNavigate.invoke()
                }
            }

            OutlinedTextField(
                value = detailUIState.title,
                onValueChange = { detailReminderViewModel?.onTitleChange(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = detailUIState.description,
                onValueChange = { detailReminderViewModel?.onDescriptionChange(it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reminder Time: ${reminderTime?.let { formatDate(it) } ?: formatDate(detailUIState.reminderTime)}",
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable { showDatePicker = true },
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (reminderTime != null) {
                            detailReminderViewModel?.onReminderTimeChange(reminderTime!!)
                        }
                        if (isNewReminder) {
                            detailReminderViewModel?.addReminder()
                            detailReminderViewModel?.scheduleReminderNotification(reminderId = "new", reminderTime = reminderTime!!)
                        }else{
                            detailReminderViewModel?.updateReminder( reminderId)
                            detailReminderViewModel?.scheduleReminderNotification(reminderId, reminderTime!!)
                        }
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF253334)
                    )
                ) {
                    Text("Save", color = Color.White)
                }

                Button(
                    onClick = { onCancel.invoke() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red
                    )
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    showDatePicker = false
                    showTimePicker = true
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        if (showTimePicker) {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    reminderTime = Timestamp(calendar.time)
                    showTimePicker = false
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
}

private fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM-dd-yy hh:mm a", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}


@Preview(showBackground = true)
@Composable
fun PreviewAddReminderScreen() {
    val navController = rememberNavController()

    AddReminderScreen(
        detailReminderViewModel = null,
        navController = navController,
        reminderId = "",
        onNavigate = {},
        onCancel = {
            println("Reminder creation cancelled")
        }
    )
}
