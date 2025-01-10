package com.example.noteapplication.utils

object Constants {

    object NavGraphs{
        const val LOGIN_SCREENS = "login_nav"
        const val NOTE_SCREENS = "note_nav"
    }

    object User{
        const val TABLE_NAME = "user"

        object ColumnNames {
            const val USER_ID = "user_id"
            const val FIRST_NAME = "first_name"
            const val LAST_NAME = "last_name"
            const val EMAIL = "email"
            const val PASSWORD = "password"
            const val DATE_REGISTERED = "date_registered"
            const val SYNC_FLAG = "sync_flag"
        }
    }

    enum class DateFormats(val sdfFormat: String) {
        SPREAD("yyyy-MM-dd HH:mm:ss"),
        ISO8601("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
        CONCAT_FILE("yyyyMMddHHmmss"),
        SLASHED_D_M_Y("dd/MM/yyyy"),
        DEFAULT("yyyy-MM-dd"),
        DATE_FORMAT_HYPHEN_DMY("dd-MM-yyyy"),
        UI_DATE_FORMAT("MMM dd, yyyy")
    }

    enum class NoteCategory {
        ALL,
        WORK,
        READING,
        IMPORTANT
    }

    enum class MediaTypes(){
        IMAGES,
        VIDEOS
    }

}

