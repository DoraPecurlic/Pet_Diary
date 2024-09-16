package com.example.petdiary.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Reminders (
    val userId: String = "",
    val title: String ="",
    val description: String ="",
    val reminderTime: Timestamp = Timestamp.now(),
    @get:PropertyName("isDone") @set:PropertyName("isDone") var isDone: Boolean = false,
    val documentId: String = ""


)
