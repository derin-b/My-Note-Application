package com.example.noteapplication.presentation.noteDetail

import com.example.noteapplication.domain.models.Media
import com.example.noteapplication.utils.Constants


sealed class NoteDetailEvent{
    data class EnteredTitle(val title: String): NoteDetailEvent()
    data class EnteredDescription(val description: String): NoteDetailEvent()
    data class Category(val category: String): NoteDetailEvent()
    data class OnMediaSelected(val media: Media): NoteDetailEvent()
    data class OnMediaRemoved(val mediaType: Constants.MediaTypes): NoteDetailEvent()
    data object SaveNote: NoteDetailEvent()
}

sealed class NoteDetailUiEvent {
    data object SaveNote: NoteDetailUiEvent()
}

