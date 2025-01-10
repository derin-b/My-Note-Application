package com.example.noteapplication.data.entity.note

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    val noteId: String,
    val title: String,
    val description: String,
    val noteCategory: String,
    val mediaList: String = "",
    var mediaId: String,
    val userId: String,
    val dateCreated: String,
    var syncFlag: Int = 0,
    )
