package com.example.noteapplication.domain.useCase

import com.example.noteapplication.data.repository.note.NoteRepository
import com.example.noteapplication.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class UploadNotesUseCase(private val noteRepository: NoteRepository) {

    // execute the upload notes to Firebase operation
    suspend fun execute(): Results<Unit> {
        return try {
            coroutineScope {
                val notes = withContext(Dispatchers.IO) {
                    noteRepository.getAllNotesToUpload()
                }

                val uploadTasks = notes.map { note ->
                    async {
                        try {
                            // Step 1: upload media only if mediaList is not empty
                            if (note.mediaList.isNotEmpty()) {
                                val mediaUploadResult = noteRepository.uploadMediaToFirebase(note)
                                when (mediaUploadResult) {
                                    is Results.Success -> {
                                        val mediaDownloadUrls = mediaUploadResult.data
                                        val mediaIds = mediaDownloadUrls.joinToString(",") { it.toString() }
                                        note.mediaId = mediaIds

                                        // Step 2: upload the note after successful media upload
                                        val noteUploadResult = noteRepository.uploadNoteToFirebase(note)
                                        if (noteUploadResult is Results.Failure) {
                                            throw Exception("Failed to upload note: ${note.noteId}")
                                        }
                                    }
                                    is Results.Failure -> {
                                        throw Exception("Failed to upload media for note: ${note.noteId}")
                                    }
                                }
                            } else {
                                // if mediaList is empty, upload the note without media
                                val noteUploadResult = noteRepository.uploadNoteToFirebase(note)
                                if (noteUploadResult is Results.Failure) {
                                    throw Exception("Failed to upload note: ${note.noteId}")
                                }
                            }
                        } catch (e: Exception) {
                            throw e
                        }
                    }
                }

                // Wait for all upload tasks to complete
                uploadTasks.awaitAll()
            }

            Results.Success(Unit)  // Return success if all uploads are done
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }
}





