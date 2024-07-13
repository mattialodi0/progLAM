package com.example.proglam.db

import androidx.lifecycle.LiveData
import java.util.Date


class ActivityRecordRepository(private val activityRecordDao: ActivityRecordDao) {

    val getRecentActivityRecords: LiveData<List<ActivityRecord>> = activityRecordDao.getRecentActivityRecords(10)
    //val getTodayActivitiesNumber: LiveData<Int> = activityRecordDao.getTodayActivitiesNumber(System.currentTimeMillis()-86400000L)
     val getTodayActivitiesNumber: LiveData<Int> = activityRecordDao.getTodayActivitiesNumber(Date(Date().time - Date().time % (24 * 60 * 60 * 1000)).toInstant().toEpochMilli() - (2*60*60*1000))
    val getFiveDaysActivities: LiveData<List<ActivityRecord>> = activityRecordDao.getPastDaysActivities(System.currentTimeMillis()-(5*86400000L))


    fun addActivityRecord(activityRecord: ActivityRecord) {
        activityRecordDao.addActivityRecord(activityRecord)
    }
    fun deleteActivityRecord(id: Int) {
        activityRecordDao.deleteActivityRecord(id)
    }

    fun getActivitiesFromTo(from: Long, to: Long): List<ActivityRecord> {
        return activityRecordDao.getActivitiesFromTo(from, to)
    }

    fun getActivityById(id: Int): ActivityRecord {
        return activityRecordDao.getActivityById(id)
    }

    fun findActivitiesForCharts(type: Int, timeRange: Int): List<Any> {
        when(type) {
            0 -> {
                var startTime = System.currentTimeMillis()
                startTime -= when(timeRange) {
                    2 -> 86400000L*336
                    1 -> 86400000L*28
                    else -> 86400000L*7
                }
                return  activityRecordDao.getActivitiesWithType(startTime)
            }
            1 -> {
                var startTime = System.currentTimeMillis()
                startTime -= when(timeRange) {
                    2 -> 86400000L*336
                    1 -> 86400000L*28
                    else -> 86400000L*7
                }
                return  activityRecordDao.getActivitiesSteps(startTime)
            }
            2 -> {
                var startTime = System.currentTimeMillis()
                startTime -= when(timeRange) {
                    2 -> 86400000L*336
                    1 -> 86400000L*28
                    else -> 86400000L*7
                }
                return  activityRecordDao.getActivitiesMeanTimeByDay(startTime)
            }
            else -> {
                var startTime = System.currentTimeMillis()
                startTime -= when(timeRange) {
                    2 -> 86400000L*356
                    1 -> 86400000L*28
                    else -> 86400000L*7
                }
                return  activityRecordDao.getActivitiesVehicles(startTime)
            }
        }
    }
}
