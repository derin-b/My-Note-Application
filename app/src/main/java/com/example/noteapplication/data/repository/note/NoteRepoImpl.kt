package com.example.noteapplication.data.repository.note

import android.net.Uri
import android.util.Log
import com.example.noteapplication.data.dao.note.NoteDao
import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.domain.models.Media
import com.example.noteapplication.domain.models.NoteDetailUIState
import com.example.noteapplication.utils.Constants
import com.example.noteapplication.utils.Results
import com.example.noteapplication.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class NoteRepoImpl(
    private val noteDao: NoteDao,
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage
    ): NoteRepository {
    override suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    override suspend fun insert(note: List<Note>) {
        noteDao.insert(note)
    }

    override suspend fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }

    override suspend fun getAllNotesToUpload(): List<Note> {
        return noteDao.getAllNotesToUpload()
    }

    override suspend fun getNoteById(noteId: String): Flow<Note?> {
        return noteDao.getNoteById(noteId)
    }

    override suspend fun deleteNoteById(noteId: String) {
        noteDao.deleteNoteById(noteId)
    }

    override suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    override suspend fun uploadNoteToFirebase(notes: Note): Results<Unit> {
        return try {
            // prepare the note data to upload
            val noteData = hashMapOf(
                "noteId" to notes.noteId,
                "title" to notes.title,
                "description" to notes.description,
                "noteCategory" to notes.noteCategory,
                "mediaId" to notes.mediaId,
                "userId" to notes.userId,
                "dateCreated" to notes.dateCreated,
            )

            // perform the upload operation without timeout handling here
            firebaseFireStore.collection("notes")
                .document(notes.noteId)
                .set(noteData)
                .await() // await the result of the upload operation

            // return success if the note was uploaded successfully
            Results.Success(Unit)
        } catch (e: Exception) {
            // catch any exceptions and return a failure result
            Results.Failure(e)
        }
    }

    override suspend fun fetchNotesFromFirebase(): Results<List<Note>> {
        return try {
            // ensure the user ID is not null
            val currentUserId = firebaseAuth.currentUser?.uid
                ?: return Results.Failure(Exception("User not logged in"))

            // use withTimeoutOrNull to limit the operation's duration
            val notes = withTimeoutOrNull(50000L) {
                firebaseFireStore.collection("notes")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .await()
                    .map { doc ->
                        Note(
                            noteId = doc.getString("noteId") ?: "",
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            noteCategory = doc.getString("noteCategory") ?: "",
                            mediaId = doc.getString("mediaId") ?: "",
                            userId = doc.getString("userId") ?: "",
                            dateCreated = doc.getString("dateCreated") ?: "",
                            syncFlag = 1
                        )
                    }
            }

            // handle timeout case
            if (notes == null) {
                Results.Failure(Exception("Fetching notes timed out"))
            } else {
                Results.Success(notes)
            }
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    override suspend fun deleteNoteFromFirebase(noteId: String): Results<Unit> {
        return try {
            // delete the note document from the firebaseFireStore collection using the note ID
            firebaseFireStore.collection("notes")
                .document(noteId) // use the note ID as the document ID for deletion
                .delete()
                .await() // await the completion of the delete operation

            // return a success result if the note was deleted successfully
            Results.Success(Unit)
        } catch (e: Exception) {
            // return a failure result containing the exception for further handling
            Results.Failure(e)
        }

    }

    override suspend fun uploadNotesToFirebase(): Results<Unit> {
        return try {
            // use the current coroutine scope to launch async tasks in parallel
            coroutineScope {
                // fetch notes to upload
                val notes = noteDao.getAllNotesToUpload()

                // launch asynchronous uploads for each note in parallel
                val uploadTasks = notes.map { note ->
                    async {
                        try {
                            val noteData = hashMapOf(
                                "noteId" to note.noteId,
                                "title" to note.title,
                                "description" to note.description,
                                "noteCategory" to note.noteCategory,
                                "mediaId" to note.mediaId,
                                "userId" to note.userId,
                                "dateCreated" to note.dateCreated,
                            )

                            // perform the upload to Fire store
                            firebaseFireStore.collection("notes")
                                .document(note.noteId)
                                .set(noteData)
                                .await() // await the result of the upload operation
                        } catch (e: Exception) {
                            // Log or handle any exceptions specific to the async task
                            throw e // rethrow the error to be caught in the parent scope
                        }
                    }
                }

                // await all the async uploads to finish
                uploadTasks.awaitAll()
            }

            // return success if all notes are uploaded successfully
            Results.Success(Unit)
        } catch (e: Exception) {
            // catch any exceptions from the coroutine scope or individual async tasks
            Results.Failure(e)
        }
    }

    override suspend fun uploadMediaToFirebase(note: Note): Results<List<Uri>> {
        val downloadUrls = mutableListOf<Uri>()
        // Convert JSON string back to list

        val mediaList = Utils.getListFromString(note.mediaList)

        try {
            coroutineScope {
               mediaList.map { media ->
                    async {
                        val fileName = note.noteId
                        val storageRef = when (media.type) {
                            Constants.MediaTypes.IMAGES -> firebaseStorage.reference.child("notes_images/$fileName.jpg")
                            Constants.MediaTypes.VIDEOS -> firebaseStorage.reference.child("notes_videos/$fileName.mp4")
                            else -> throw IllegalArgumentException("Unsupported media type: ${media.type}")
                        }

                        storageRef.putFile(Uri.parse(media.uri)).await() // Upload file
                        val downloadUrl = storageRef.downloadUrl.await() // Get download URL
                        downloadUrls.add(downloadUrl)
                    }
                }.awaitAll() // Wait for all uploads to finish
            }
            return Results.Success(downloadUrls) // Return success with the list of URLs
        } catch (e: Exception) {
            return Results.Failure(e) // Return failure with the exception
        }
    }


}