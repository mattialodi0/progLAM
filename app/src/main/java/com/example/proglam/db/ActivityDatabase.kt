package com.example.proglam.db

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [ActivityType::class, ActivityRecord::class],
    version = 5,
    exportSchema = false
)
abstract class ActivityDatabase : RoomDatabase() {
    abstract fun activityTypeDao(): ActivityTypeDao
    abstract fun activityRecordDao(): ActivityRecordDao

    companion object {
        @Volatile
        private var INSTANCE: ActivityDatabase? = null

        fun getDatabase(context: Context): ActivityDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ActivityDatabase::class.java,
                    "activity_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()       //.createFromAsset("database/activities.db")

                INSTANCE = instance
                return instance
            }
        }



    }

}