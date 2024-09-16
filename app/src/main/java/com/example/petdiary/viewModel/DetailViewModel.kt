package com.example.petdiary.viewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.petdiary.models.Notes
import com.example.petdiary.repository.StorageRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class DetailViewModel(
    private val repository: StorageRepository
) : ViewModel(){
    var noteUIState by mutableStateOf(NoteUIState())
        private set

    private val hasUser: Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user

    fun onColorChange(colorIndex: Int) {
        noteUIState = noteUIState.copy(colorIndex = colorIndex)
    }

    fun onTitleChange(title: String) {
        noteUIState = noteUIState.copy(title = title)
    }

    fun onNoteChange(note: String) {
        noteUIState = noteUIState.copy(note = note)
    }

    fun addNote(){
        if (hasUser){
            repository.addNote(
                userId = user!!.uid,
                title = noteUIState.title,
                description = noteUIState.note,
                color = noteUIState.colorIndex,
                timestamp = Timestamp.now()
            ){
                noteUIState = noteUIState.copy(noteAddedStatus = it)
            }
        }



    }

    fun setEditFields(note: Notes){
        noteUIState = noteUIState.copy(
            colorIndex = note.colorIndex,
            title = note.title,
            note = note.description
        )

    }
    fun getNote(noteId:String){
        repository.getNote(
            noteId = noteId,
            onError = {},
        ){
            noteUIState = noteUIState.copy(selectedNote = it)
            noteUIState.selectedNote?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateNote(
        noteId: String
    ){
        repository.updateNote(
            title = noteUIState.title,
            note = noteUIState.note,
            noteId = noteId,
            color = noteUIState.colorIndex
        ){
            noteUIState = noteUIState.copy(updatedNoteStatus = it)
        }
    }

    fun resetNoteAddedStatus(){
        noteUIState = noteUIState.copy(
            noteAddedStatus = false,
            updatedNoteStatus = false,
        )
    }

    fun resetState(){
        noteUIState = NoteUIState()
    }




}

data class NoteUIState(
    val colorIndex: Int = 0,
    val title: String="",
    val note: String ="",
    val noteAddedStatus:Boolean = false,
    val updatedNoteStatus: Boolean  = false,
    val selectedNote: Notes? = null
)