package com.example.petdiary.viewModel

import android.app.Application

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

import com.example.petdiary.models.Reminders

import com.example.petdiary.repository.StorageRepository
import com.example.petdiary.utilities.NotificationHelper
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class DetailReminderViewModel(
    application: Application,
    private val repository: StorageRepository = StorageRepository()


) : AndroidViewModel(application) {

    private val notificationHelper = NotificationHelper(application)

    var reminderUIState by mutableStateOf(RemindersUIState())
        private set

    private val hasUser: Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user

    fun onTitleChange(title: String) {
        reminderUIState = reminderUIState.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        reminderUIState = reminderUIState.copy(description = description)
    }

    fun onReminderTimeChange(reminderTime: Timestamp){
        reminderUIState = reminderUIState.copy(reminderTime = reminderTime)
    }

    fun scheduleReminderNotification(reminderId: String, reminderTime: Timestamp) {
        notificationHelper.scheduleReminderNotification(
            reminderId = reminderId,
            title = reminderUIState.title,
            description = reminderUIState.description,
            reminderTime = reminderTime
        )
    }



    fun addReminder() {
        if (hasUser) {
            repository.addReminder(
                userId = user!!.uid,
                title = reminderUIState.title,
                description = reminderUIState.description,
                reminderTime = reminderUIState.reminderTime,
                isDone = reminderUIState.isDone
            ) {
                reminderUIState = reminderUIState.copy(reminderAddedStatus = it)



            }
        }
    }

    fun getReminder(reminderId:String){
        repository.getReminder(
            reminderId = reminderId,
            onError = {},
        ){
            reminderUIState = reminderUIState.copy(selectedReminder = it)
            reminderUIState.selectedReminder?.let { it1 -> setEditFields(it1) }
        }
    }
    fun updateReminder(

        reminderId: String
    ){
        repository.updateReminder(
            title = reminderUIState.title,
            description = reminderUIState.description,
            reminderTime = reminderUIState.reminderTime,
            reminderId = reminderId


        ){
            reminderUIState = reminderUIState.copy(updatedReminderStatus = it)


        }
    }
    fun resetState(){
        reminderUIState = RemindersUIState()
    }
    fun resetReminderAddedStatus(){
        reminderUIState = reminderUIState.copy(
            reminderAddedStatus = false,
            updatedReminderStatus = false,
        )
    }
    fun setEditFields(reminder: Reminders){
        reminderUIState = reminderUIState.copy(
            title = reminder.title,
            description = reminder.description,
            reminderTime = reminder.reminderTime
        )

    }



}


data class RemindersUIState(
    val title: String = "",
    val description: String = "",
    val reminderTime: Timestamp = Timestamp.now(),
    val isDone: Boolean = false,
    val reminderAddedStatus: Boolean = false,
    val updatedReminderStatus: Boolean = false,
    val selectedReminder: Reminders? = null
)













