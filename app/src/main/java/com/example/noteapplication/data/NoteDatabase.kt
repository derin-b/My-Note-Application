package com.example.noteapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.noteapplication.data.dao.note.NoteDao
import com.example.noteapplication.data.dao.user.UserDao
import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.data.entity.user.User

@Database(
    entities = [
        User::class,
        Note::class
               ],
    version = 1,
    exportSchema = true
)

abstract class NoteDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var Instance: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = NoteDatabase::class.java,
                    name = "note.db"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }

}