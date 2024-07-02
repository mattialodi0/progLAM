package com.example.proglam.db

import android.util.Log
import androidx.lifecycle.LiveData
import java.sql.Timestamp

class ActivityRecordRepository(private val activityRecordDao: ActivityRecordDao) {

    val getActivityRecords: LiveData<List<ActivityRecord>> = activityRecordDao.getActivityRecords()
    val getRecentActivityRecords: LiveData<List<ActivityRecord>> = activityRecordDao.getRecentActivityRecords(5)
    val getTodayActivitiesNumber: LiveData<Int> = activityRecordDao.getTodayActivitiesNumber(System.currentTimeMillis()-86400000L)
    val getTodayActivities: LiveData<List<ActivityRecord>> = activityRecordDao.getTodayActivities(System.currentTimeMillis()-86400000L)
    val getFiveDaysActivities: LiveData<List<ActivityRecord>> = activityRecordDao.getPastDaysActivities(System.currentTimeMillis()-(5*86400000L))


    suspend fun addActivityRecord(activityRecord: ActivityRecord) {
        activityRecordDao.addActivityRecord(activityRecord)
    }
    suspend fun deleteActivityRecord(id: Int) {
        activityRecordDao.deleteActivityRecord(id)
    }

    suspend fun getActivitiesFromTo(from: Long, to: Long): List<ActivityRecord> {
        return activityRecordDao.getActivitiesFromTo(from, to)
    }

    suspend fun getActivityById(id: Int): ActivityRecord {
        return activityRecordDao.getActivityById(id)
    }
}