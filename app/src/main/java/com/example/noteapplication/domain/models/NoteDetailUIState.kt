package com.example.noteapplication.domain.models

import android.net.Uri
import com.example.noteapplication.utils.Constants

data class NoteDetailUIState(
    val title: String = "",
    val description: String = "",
    val selectedCategory: String = "Important",
    val mediaId: String = "",
    val noteId: String = "",
    val mediaList : List<Media> = emptyList(),
)

data class Media(
    val uri: String,
    val type: Constants.MediaTypes
)
