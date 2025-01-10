package com.example.noteapplication.domain.useCase

import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.data.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddNote(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        withContext(Dispatchers.IO) {
        repository.insert(note)
        }
    }
}