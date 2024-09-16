package com.example.petdiary.view






import android.app.Activity
import android.content.Intent
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.petdiary.models.Reminders
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.petdiary.repository.Resources
import com.google.firebase.Timestamp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.petdiary.MainActivity
import com.example.petdiary.viewModel.RemindersPageUIState
import com.example.petdiary.viewModel.RemindersViewModel
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RemindersScreen(
    remindersViewModel: RemindersViewModel?,
    onReminderClick: (id: String) -> Unit,
    navToDetailReminderPage: () -> Unit,
    navToLoginPage: () -> Unit,
    navController: NavHostController
) {
    val remindersPageUIState = remindersViewModel?.remindersPageUIState ?: RemindersPageUIState()

    var openDialog by remember {
        mutableStateOf(false)
    }

    var selectedReminder: Reminders? by remember {
        mutableStateOf(null)
    }

    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        remindersViewModel?.loadReminders()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navToDetailReminderPage.invoke() },
                backgroundColor = Color(0xFF253334),
                modifier = Modifier
                    .padding(bottom = 50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                backgroundColor = Color(0xFF253334),
                navigationIcon = {},
                actions = {
                    IconButton(onClick = {
                        remindersViewModel?.signOut()


                        val intent = Intent(context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .background(Color(0xB2FFFFFF))
                        )
                    }
                },
                title = {
                    Text(
                        text = "Reminders",
                        color = Color(0xB2FFFFFF),
                        modifier = Modifier.padding(top = 15.dp),
                        fontSize = 25.sp,
                    )
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (remindersPageUIState.remindersList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                is Resources.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(remindersPageUIState.remindersList.data ?: emptyList()) { reminder ->
                            ReminderItem(
                                reminders = reminder,
                                onLongClick = {
                                    openDialog = true
                                    selectedReminder = reminder
                                },
                            ) {
                                onReminderClick.invoke(reminder.documentId)
                                navController.navigate("add_reminder/${reminder.documentId}")
                            }
                        }
                    }

                    AnimatedVisibility(visible = openDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog = false
                            },
                            title = { Text(text = "Delete Reminder?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedReminder?.documentId?.let {
                                            remindersViewModel?.deleteReminder(it)
                                        }
                                        openDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Red
                                    ),
                                ) {
                                    Text(text = "Delete")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { openDialog = false }) {
                                    Text(text = "Cancel")
                                }
                            }
                        )
                    }
                }
                else -> {
                    Text(
                        text = remindersPageUIState.remindersList.throwable?.localizedMessage ?: "Unknown Error",
                        color = Color.Red
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReminderItem(reminders: Reminders, onLongClick: () -> Unit, onClick: () -> Unit,) {

    Card(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick.invoke() },
                onClick = { onClick.invoke() }
            )
            .padding(8.dp)
            .fillMaxWidth(),

    ){
        Column {
            Text(
                text = reminders.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = reminders.description,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 4
                )

            }
            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = formatDate(reminders.reminderTime),
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.End),
                    maxLines = 4
                )

            }


        }


    }

}

private fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM-dd-yy hh:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

@Preview
@Composable
fun PreviewRemindersScreen() {

    val navController = rememberNavController()
    RemindersScreen(
        remindersViewModel = null ,
        onReminderClick = {},
        navToDetailReminderPage = { /*TODO*/ },
        navToLoginPage = { /*TODO*/ },
        navController = navController
    )
}
