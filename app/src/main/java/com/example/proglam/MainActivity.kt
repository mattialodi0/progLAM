package com.example.proglam

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.example.proglam.databinding.ActivityMainBinding
import com.example.proglam.utils.Permissions
import java.util.concurrent.TimeUnit
import androidx.work.PeriodicWorkRequest
import com.example.proglam.background.ReminderWorker
import com.example.proglam.db.ActivityRecord
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.db.ActivityType
import com.example.proglam.db.ActivityTypeViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        /* UI setup */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()

        /* DB setup */
        //populateDatabase()

        /* background setup */
        Permissions.needsPermission(this, if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.POST_NOTIFICATIONS else "POST_NOTIFICATIONS")
        setWorker()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        val navView: BottomNavigationView = binding.navView

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_startActivity, R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.navigation_calendarActivity || destination.id == R.id.navigation_newActivity
                    || destination.id == R.id.navigation_activityRecord) {
                navView.visibility = View.GONE
            }
            else {

                navView.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setWorker() {
        val mWorkManager = WorkManager.getInstance(applicationContext)

        // one time
        /*val uploadRequest = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
            .build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.getWorkInfoByIdLiveData(uploadRequest.id)
            .observe(this, Observer {
                Log.i("reminderWork", it.state.name.toString())
            })
        workManager.enqueue(uploadRequest)
*/


        // periodic
        val request: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            ReminderWorker::class.java, 10, TimeUnit.HOURS, 5, TimeUnit.MINUTES)
            //.setInitialDelay(5, TimeUnit.SECONDS)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 3, TimeUnit.SECONDS)
            .build()
        mWorkManager.enqueueUniquePeriodicWork(
            "reminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun populateDatabase() {
        val mActivityTypeViewModel = ViewModelProvider(this).get(ActivityTypeViewModel::class.java)

        val activityType1 = ActivityType(0,"walk", "",  "ic_activitytype_walk", 10)
        val activityType2 = ActivityType(0,"run", "",  "ic_activitytype_run", 11)
        val activityType3 = ActivityType(0,"rest", "",  "ic_activitytype_rest", 0)
        val activityType4 = ActivityType(0,"sleep", "",  "ic_activitytype_sleep", 0)
        val activityType5 = ActivityType(0,"in vehicle", "",  "ic_activitytype_car", 1)

        mActivityTypeViewModel.addActivityType(activityType1)
        mActivityTypeViewModel.addActivityType(activityType2)
        mActivityTypeViewModel.addActivityType(activityType3)
        mActivityTypeViewModel.addActivityType(activityType4)
        mActivityTypeViewModel.addActivityType(activityType5)
    }
}