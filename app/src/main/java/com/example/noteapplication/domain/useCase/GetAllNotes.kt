package com.example.noteapplication.domain.useCase

import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.data.repository.note.NoteRepository
import com.example.noteapplication.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllNotes(
    private val repository: NoteRepository
) {
    suspend fun execute(searchText: String, category: Constants.NoteCategory): Flow<List<Note>> {
        return repository.getAllNotes().map { notes ->
            when (category) {
                Constants.NoteCategory.WORK -> notes.filter {
                    it.noteCategory.equals("Work", ignoreCase = true) && it.title.contains(searchText, ignoreCase = true)
                }
                Constants.NoteCategory.ALL -> notes.filter {
                    it.title.contains(searchText, ignoreCase = true)
                }
                Constants.NoteCategory.READING -> notes.filter {
                    it.noteCategory.equals("Reading", ignoreCase = true) && it.title.contains(searchText, ignoreCase = true)
                }
                Constants.NoteCategory.IMPORTANT -> notes.filter {
                    it.noteCategory.equals("Important", ignoreCase = true) && it.title.contains(searchText, ignoreCase = true)
                }
            }
        }
    }

}