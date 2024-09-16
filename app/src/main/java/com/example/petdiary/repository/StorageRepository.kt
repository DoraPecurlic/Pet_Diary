package com.example.petdiary.repository

import com.example.petdiary.models.Notes
import com.example.petdiary.models.Reminders
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore


class StorageRepository () {
    val user = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty() //ako je user null

    private val notesRef = FirebaseFirestore.getInstance().collection("notes")
    private val remindersRef = FirebaseFirestore.getInstance().collection("reminders")



    fun getUserNotes(
        userId: String
    ): Flow<Resources<List<Notes>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = notesRef
                .orderBy("timestamp")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val notes = snapshot.toObjects(Notes::class.java)
                        Resources.Success(data = notes)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }


        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }

    }

    fun getNote(
        noteId:String,
        onError:(Throwable?) -> Unit,
        onSuccess: (Notes?) -> Unit
    ){
        notesRef
            .document(noteId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Notes::class.java))
            }
            .addOnFailureListener {result ->
                onError.invoke(result.cause)
            }


    }

    fun addNote(
        userId: String,
        title: String,
        description: String,
        timestamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit,
    ){
        val documentId = notesRef.document().id
        val note = Notes(
            userId,
            title,
            description,
            timestamp,
            colorIndex = color,
            documentId = documentId
        )
        notesRef
            .document(documentId)
            .set(note)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteNote(noteId: String,onComplete: (Boolean) -> Unit){
        notesRef.document(noteId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateNote(
        title: String,
        note:String,
        color: Int,
        noteId: String,
        onResult:(Boolean) -> Unit
    ){
        val updateData = hashMapOf<String,Any>(
            "colorIndex" to color,
            "description" to note,
            "title" to title,
        )

        notesRef.document(noteId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }



    }


    fun signOut() = Firebase.auth.signOut()



    fun getUserReminders(
        userId: String
    ): Flow<Resources<List<Reminders>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = remindersRef
                .orderBy("reminderTime")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val reminders = snapshot.toObjects(Reminders::class.java)
                        Resources.Success(data = reminders)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }


        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }

    }

    fun deleteReminder(reminderId: String,onComplete: (Boolean) -> Unit){
        remindersRef.document(reminderId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun addReminder(
        userId: String,
        title: String,
        description: String,
        reminderTime: Timestamp,
        isDone: Boolean = false,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = remindersRef.document().id
        val reminder = Reminders(
            userId = userId,
            title = title,
            description = description,
            reminderTime = reminderTime,
            isDone = isDone,
            documentId = documentId
        )

        remindersRef
            .document(documentId)
            .set(reminder)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun getReminder(
        reminderId:String,
        onError:(Throwable?) -> Unit,
        onSuccess: (Reminders?) -> Unit
    ){
        remindersRef
            .document(reminderId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Reminders::class.java))
            }
            .addOnFailureListener {result ->
                onError.invoke(result.cause)
            }


    }

    fun updateReminder(
        title: String,
        description:String,
        reminderTime: Timestamp,
        reminderId: String,
        onResult:(Boolean) -> Unit
    ){
        val updateData = hashMapOf<String,Any>(
            "reminderTime" to reminderTime,
            "description" to description,
            "title" to title,
        )

        remindersRef.document(reminderId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }



    }





}



sealed class  Resources<T>(
    val data:T? = null,
    val throwable: Throwable? = null
){
    class Loading<T>:Resources<T>()
    class Success<T>(data: T?): Resources<T>(data = data)
    class Error<T>(throwable: Throwable?):Resources<T>(throwable = throwable)
}