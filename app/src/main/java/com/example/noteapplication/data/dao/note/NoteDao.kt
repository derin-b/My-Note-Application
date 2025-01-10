package com.example.noteapplication.data.dao.note

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notes: List<Note>)

    @Query(
        "SELECT * FROM notes " +
                "ORDER BY dateCreated"
    )
    fun getAllNotes(): Flow<List<Note>>

    @Query(
        "SELECT * FROM notes " +
                "WHERE noteId=:noteId "
    )
    fun getNoteById(noteId: String): Flow<Note?>

    @Query(
        "DELETE FROM notes " +
                "WHERE noteId = :noteId"
    )
    fun deleteNoteById(noteId: String)

    @Query("DELETE FROM notes")
    fun deleteAllNotes()

    @Query(
        "SELECT * FROM notes " +
                "WHERE syncFlag = 0"
    )
    fun getAllNotesToUpload(): List<Note>

}