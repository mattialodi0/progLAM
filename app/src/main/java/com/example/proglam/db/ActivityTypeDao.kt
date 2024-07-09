package com.example.proglam.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ActivityTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addActivityType(activityType: ActivityType)

    @Delete
    fun removeActivityType(activityType: ActivityType)

    @Query("SELECT * FROM activityType_table")
    fun getActivityTypes() : LiveData<List<ActivityType>>

    @Query("SELECT * FROM activityType_table WHERE name = :name")
    fun findActivityTypeByName(name: String): ActivityType

    @Query("SELECT COUNT(*) FROM activityType_table")
    fun getActivityTypeCount(): Int
}