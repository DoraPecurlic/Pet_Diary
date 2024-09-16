package com.example.petdiary.viewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petdiary.models.Notes
import com.example.petdiary.repository.Resources
import com.example.petdiary.repository.StorageRepository
import kotlinx.coroutines.launch

class NotesViewModel(
    private val repository: StorageRepository = StorageRepository(),
) : ViewModel() {
    var notesPageUIState by mutableStateOf(NotesPageUIState())

    val user = repository.user
    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    fun loadNotes(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUserNotes(userId)
            }
        }else{
            notesPageUIState = notesPageUIState.copy(notesList = Resources.Error(
                throwable = Throwable(message = "User is not Login")
            ))
        }
    }

    private fun getUserNotes(userId:String) = viewModelScope.launch {
        repository.getUserNotes(userId).collect {
            notesPageUIState = notesPageUIState.copy(notesList = it)
        }
    }

    fun deleteNote(noteId:String) = repository.deleteNote(noteId){
        notesPageUIState = notesPageUIState.copy(noteDeletedStatus = it)
    }

    fun signOut() = repository.signOut()


}


data class NotesPageUIState(
    val notesList: Resources<List<Notes>> = Resources.Loading(),
    val noteDeletedStatus: Boolean = false,
)