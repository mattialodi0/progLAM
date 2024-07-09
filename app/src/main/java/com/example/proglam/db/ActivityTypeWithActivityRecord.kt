package com.example.proglam.db

import androidx.room.Embedded
import androidx.room.Relation

data class ActivityTypeWithActivityRecord(
    @Embedded val activityRecord: ActivityRecord,
    @Relation(
        parentColumn = "type",
        entityColumn = "name"
    )
    val activityType: ActivityType
)
