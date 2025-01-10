package com.example.noteapplication.domain.useCase

import com.example.noteapplication.data.repository.note.NoteRepository
import com.example.noteapplication.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteNote(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteId: String) {
        when(val fetchResult = repository.deleteNoteFromFirebase(noteId)){
            is Results.Success ->{
                withContext(Dispatchers.IO) {
                    repository.deleteNoteById(noteId)
                }
            }
            is Results.Failure -> fetchResult.exception.message
        }
    }
}