package com.example.proglam.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ActivityRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addActivityRecord(activityRecord: ActivityRecord)

    @Query("DELETE FROM activityRecord_table WHERE id = :id")
    fun deleteActivityRecord(id: Int)

    @Query("SELECT * FROM activityRecord_table")
    fun getActivityRecords(): LiveData<List<ActivityRecord>>

    @Query("SELECT COUNT(*) FROM activityRecord_table WHERE startTime > :todayTimestamp")
    fun getTodayActivitiesNumber(todayTimestamp: Long): LiveData<Int>

    @Query("SELECT * FROM activityRecord_table WHERE startTime > :todayTimestamp")
    fun getTodayActivities(todayTimestamp: Long): LiveData<List<ActivityRecord>>

    @Query("SELECT * FROM activityRecord_table ORDER BY startTime DESC LIMIT :limit")
    fun getRecentActivityRecords(limit: Int): LiveData<List<ActivityRecord>>

    @Query("SELECT * FROM activityRecord_table WHERE startTime > :from")
    fun getPastDaysActivities(from: Long): LiveData<List<ActivityRecord>>

    @Query("SELECT * FROM activityRecord_table WHERE :from < startTime AND startTime < :to")
    fun getActivitiesFromTo(from: Long, to: Long): List<ActivityRecord>

    @Query("SELECT * FROM activityRecord_table WHERE id = :id")
    fun getActivityById(id: Int): ActivityRecord
}