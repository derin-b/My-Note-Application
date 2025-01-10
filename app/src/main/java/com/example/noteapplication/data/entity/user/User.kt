package com.example.noteapplication.data.entity.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.noteapplication.utils.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = Constants.User.TABLE_NAME)
data class User(
    @SerializedName(Constants.User.ColumnNames.USER_ID)
    @ColumnInfo(name = Constants.User.ColumnNames.USER_ID)
    @Expose
    @PrimaryKey
    val userId: String,

    @SerializedName(Constants.User.ColumnNames.FIRST_NAME)
    @ColumnInfo(name = Constants.User.ColumnNames.FIRST_NAME)
    @Expose
    val firstName: String,

    @SerializedName(Constants.User.ColumnNames.LAST_NAME)
    @ColumnInfo(name = Constants.User.ColumnNames.LAST_NAME)
    @Expose
    val lastName: String,

    @SerializedName(Constants.User.ColumnNames.EMAIL)
    @ColumnInfo(name = Constants.User.ColumnNames.EMAIL)
    @Expose
    val email: String,

    @SerializedName(Constants.User.ColumnNames.PASSWORD)
    @ColumnInfo(name = Constants.User.ColumnNames.PASSWORD)
    @Expose
    val password: String,

    @SerializedName(Constants.User.ColumnNames.DATE_REGISTERED)
    @ColumnInfo(name = Constants.User.ColumnNames.DATE_REGISTERED)
    @Expose
    val dateRegistered: String,

    @SerializedName(Constants.User.ColumnNames.SYNC_FLAG)
    @ColumnInfo(name = Constants.User.ColumnNames.SYNC_FLAG)
    val synFlag: Int = 0,
)
