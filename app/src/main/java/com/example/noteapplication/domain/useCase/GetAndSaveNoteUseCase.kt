package com.example.noteapplication.domain.useCase

import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.data.repository.note.NoteRepository
import com.example.noteapplication.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAndSaveNotesUseCase(
    private val noteRepository: NoteRepository
) {
    suspend fun execute(): Results<List<Note>> {
        // Fetch the notes from Firebase
        return when (val fetchResult = noteRepository.fetchNotesFromFirebase()) {
            is Results.Success -> {
                // Insert fetched notes into the local database
                try {
                    withContext(Dispatchers.IO) {
                        noteRepository.insert(fetchResult.data)
                    }
                    Results.Success(fetchResult.data) // Return success with the notes
                } catch (e: Exception) {
                    Results.Failure(Exception("Failed to save notes locally: ${e.message}", e))
                }
            }
            is Results.Failure -> fetchResult // Propagate the failure result
        }
    }
}

