package com.example.proglam.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ActivityRecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addActivityRecord(activityRecord: ActivityRecord)

    @Query("DELETE FROM activityRecord_table WHERE id = :id")
    fun deleteActivityRecord(id: Int)

    @Query("SELECT * FROM activityRecord_table WHERE type != 'none'")
    fun getActivityRecords(): LiveData<List<ActivityRecord>>

    @Query("SELECT COUNT(*) FROM activityRecord_table WHERE startTime > :todayTimestamp AND type != 'none'")
    fun getTodayActivitiesNumber(todayTimestamp: Long): LiveData<Int>

    @Query("SELECT * FROM activityRecord_table WHERE startTime > :todayTimestamp AND type != 'none'")
    fun getTodayActivitiesList(todayTimestamp: Long): List<ActivityRecord>


    @Query("SELECT * FROM activityRecord_table  WHERE type != 'none' ORDER BY startTime DESC LIMIT :limit")
    fun getRecentActivityRecords(limit: Int): LiveData<List<ActivityRecord>>

    @Query("SELECT * FROM activityRecord_table WHERE startTime > :from AND type != 'none'")
    fun getPastDaysActivities(from: Long): LiveData<List<ActivityRecord>>

    @Query("SELECT * FROM activityRecord_table WHERE :from < startTime AND startTime < :to AND type != 'none'")
    fun getActivitiesFromTo(from: Long, to: Long): List<ActivityRecord>

    @Query("SELECT * FROM activityRecord_table WHERE id = :id")
    fun getActivityById(id: Int): ActivityRecord

    // chart queries
    @Transaction
    @Query("SELECT * FROM activityRecord_table WHERE startTime > :from AND type != 'none'")
    //@Query("SELECT type, finishTime - startTime as duration  FROM activityRecord_table WHERE startTime > :from")
    fun getActivitiesWithType(from: Long = 0): List<ActivityTypeWithActivityRecord>

    @Transaction
    @Query("SELECT * FROM activityRecord_table as R JOIN activityType_table as T ON name = type WHERE R.startTime > :from AND T.tools >= 10")
    fun getActivitiesSteps(from: Long = 0): List<ActivityRecord>

    @Transaction
    @Query("""  SELECT at.id, at.name, SUM(ar.finishTime - ar.startTime)/(1000) AS dailyTime
                FROM activityRecord_table ar INNER JOIN activityType_table at ON ar.type = at.name
                WHERE startTime > :from
                GROUP BY at.id, at.name, strftime('%d-%m-%Y', datetime(ar.startTime / 1000, 'unixepoch'))   """)
    fun getActivitiesMeanTimeByDay(from: Long = 0): List<TypeDailyTimeTuple>

    @Transaction
    @Query("""  SELECT at.name, SUM(ar.finishTime-ar.startTime)/1000 as duration 
                FROM activityRecord_table ar INNER JOIN activityType_table at ON ar.type = at.name
                WHERE startTime > :from
                GROUP BY at.name   """)
    fun getActivitiesVehicles(from: Long = 0): List<TypeDurationTuple>

}