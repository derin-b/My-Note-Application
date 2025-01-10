package com.example.noteapplication.presentation.notes


sealed class NotesScreenEvent{
    data class SearchTextChange(val text: String): NotesScreenEvent()
    data class CategoryChange(val categoryIndex: Int): NotesScreenEvent()
    data class OnDeleteClicked(val noteId: String): NotesScreenEvent()
    data object OnNoteFiltersChange: NotesScreenEvent()
    data object LogOut: NotesScreenEvent()
}

sealed class NoteScreenUIEvent {
    data object FinishActivity: NoteScreenUIEvent()
}


