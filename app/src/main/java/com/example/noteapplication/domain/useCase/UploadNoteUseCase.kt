package com.example.noteapplication.domain.useCase

import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.data.repository.note.NoteRepository
import com.example.noteapplication.utils.Results
import kotlinx.coroutines.withTimeoutOrNull

class UploadNoteUseCase(private val noteRepository: NoteRepository) {
    suspend fun execute(note: Note): Results<Unit> {
        return try {
            // use withTimeoutOrNull to apply a timeout for the Firebase operation

            // check if the operation timed out
            withTimeoutOrNull(10000L) {
                // call the repository to upload the note
                noteRepository.uploadNoteToFirebase(note)
            } // return the result from the repository
                ?: Results.Failure(IllegalStateException("Upload timeout: Please check your internet connection"))
        } catch (e: Exception) {
            // handle any other exceptions at the domain layer
            Results.Failure(e)
        }
    }
}