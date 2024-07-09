package com.example.proglam.db

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activityType_table")
data class ActivityType(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val desc: String,
    var iconSrc: String,
    val tools: Int
)