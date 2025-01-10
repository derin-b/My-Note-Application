package com.example.noteapplication.domain.useCase

import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.data.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetNote(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteId: String): Flow<Note?> {
        return withContext(Dispatchers.IO) {
            repository.getNoteById(noteId)
        }
    }
}