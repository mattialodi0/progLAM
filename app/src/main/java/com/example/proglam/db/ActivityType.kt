package com.example.proglam.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activityType_table")
data class ActivityType(
    @PrimaryKey
    val name: String,

    val desc: String,

    val iconSrc: String,

    val tools: Int
)