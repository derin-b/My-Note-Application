package com.example.noteapplication.data.repository.note

import android.net.Uri
import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.utils.Results
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun insert(note: Note)

    suspend fun insert(note: List<Note>)

    suspend fun getAllNotes(): Flow<List<Note>>

    suspend fun getAllNotesToUpload(): List<Note>

    suspend fun getNoteById(noteId:String): Flow<Note?>

    suspend fun deleteNoteById(noteId: String)

    suspend fun deleteAllNotes()

    suspend fun uploadNoteToFirebase(notes: Note) : Results<Unit>

    suspend fun fetchNotesFromFirebase(): Results<List<Note>>

    suspend fun deleteNoteFromFirebase(noteId: String): Results<Unit>

    suspend fun uploadNotesToFirebase(): Results<Unit>

    suspend fun uploadMediaToFirebase(note: Note): Results<List<Uri>>
}