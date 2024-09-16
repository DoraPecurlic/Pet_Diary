package com.example.petdiary.viewModel

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import com.example.petdiary.models.Reminders
import com.example.petdiary.repository.Resources
import com.example.petdiary.repository.StorageRepository
import com.example.petdiary.utilities.NotificationHelper


class RemindersViewModel(
    application: Application,
    private val repository: StorageRepository = StorageRepository()


) : AndroidViewModel(application) {

    var remindersPageUIState by mutableStateOf(RemindersPageUIState())

    private val notificationHelper = NotificationHelper(application)

    private val hasUser: Boolean
        get() =repository.hasUser()

    private val userId: String
        get() = repository.getUserId()

    fun loadReminders(){
        if(hasUser){
            if(userId.isNotBlank()){
                getUserReminders(userId)
            }
        }else{
            remindersPageUIState = remindersPageUIState.copy(remindersList = Resources.Error(
                throwable = Throwable(message = "User is not Login")
            ))
        }
    }

    private fun getUserReminders(userId: String) = viewModelScope.launch{
        repository.getUserReminders(userId).collect{
            remindersPageUIState = remindersPageUIState.copy(remindersList = it)
        }
    }

    fun deleteReminder(reminderId:String) = repository.deleteReminder(reminderId){
        viewModelScope.launch {
            notificationHelper.cancelReminderNotification("new")


            repository.deleteReminder(reminderId) {
                remindersPageUIState = remindersPageUIState.copy(reminderDeletedStatus = it)
            }
        }
    }

    fun signOut() = repository.signOut()



}







data class RemindersPageUIState(
    val remindersList: Resources<List<Reminders>> = Resources.Loading(),
    val reminderDeletedStatus: Boolean = false,
)
