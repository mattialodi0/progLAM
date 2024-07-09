package com.example.proglam.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "activityRecord_table")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: String,
    val startTime: Long,
    val finishTime: Long,
    val toolData: String,
)

data class TypeDurationTuple(
    @ColumnInfo(name = "name") val type: String?,
    @ColumnInfo(name = "duration") val duration: Long?
)

data class TypeDailyTimeTuple(
    @ColumnInfo(name = "id") val id: Int?,
    @ColumnInfo(name = "name") val type: String?,
    @ColumnInfo(name = "dailyTime") val dailyTime: Long?
)