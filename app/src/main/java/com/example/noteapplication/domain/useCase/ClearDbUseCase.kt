package com.example.noteapplication.domain.useCase

import com.example.noteapplication.data.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClearDbUseCase(private val noteRepository: NoteRepository) {
    suspend fun execute() {
        withContext(Dispatchers.IO) {
            noteRepository.deleteAllNotes()
        }
    }
}