package com.example.proglam.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activityRecord_table")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: String,
    val startTime: Long,
    val finishTime: Long,
    val toolData: String,
)