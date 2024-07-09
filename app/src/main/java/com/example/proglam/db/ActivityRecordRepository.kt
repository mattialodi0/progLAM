package com.example.proglam.db

import androidx.lifecycle.LiveData

class ActivityRecordRepository(private val activityRecordDao: ActivityRecordDao) {

    val getActivityRecords: LiveData<List<ActivityRecord>> = activityRecordDao.getActivityRecords()
    val getRecentActivityRecords: LiveData<List<ActivityRecord>> = activityRecordDao.getRecentActivityRecords(5)
    val getTodayActivitiesNumber: LiveData<Int> = activityRecordDao.getTodayActivitiesNumber(System.currentTimeMillis()-86400000L)
    val getTodayActivities: LiveData<List<ActivityRecord>> = activityRecordDao.getTodayActivities(System.currentTimeMillis()-86400000L)
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
                when(timeRange) {
                    2 -> startTime -= 86400000L*336
                    1 -> startTime -= 86400000L*28
                    else -> startTime -= 86400000L*7
                }
                return  activityRecordDao.getActivitiesWithType(startTime)
            }
            1 -> {
                var startTime = System.currentTimeMillis()
                when(timeRange) {
                    2 -> startTime -= 86400000L*336
                    1 -> startTime -= 86400000L*28
                    else -> startTime -= 86400000L*7
                }
                return  activityRecordDao.getActivitiesSteps(startTime)
            }
            2 -> {
                var startTime = System.currentTimeMillis()
                when(timeRange) {
                    2 -> startTime -= 86400000L*336
                    1 -> startTime -= 86400000L*28
                    else -> startTime -= 86400000L*7
                }
                return  activityRecordDao.getActivitiesMeanTimeByDay(startTime)
            }
            else -> {
                var startTime = System.currentTimeMillis()
                when(timeRange) {
                    2 -> startTime -= 86400000L*356
                    1 -> startTime -= 86400000L*30
                    else -> startTime -= 86400000L*7
                }
                return  activityRecordDao.getActivitiesVehicles(startTime)
            }
        }
    }
}
