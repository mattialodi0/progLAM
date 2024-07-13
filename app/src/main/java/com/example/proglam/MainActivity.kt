package com.example.proglam

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.proglam.background.AlarmReceiver
import com.example.proglam.databinding.ActivityMainBinding
import com.example.proglam.utils.Permissions
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

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

        /* Permission requests */
        Permissions.needsPermission(
            this,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else "POST_NOTIFICATIONS"
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (!(ActivityCompat.checkSelfPermission(
                    baseContext, Manifest.permission.BODY_SENSORS
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    baseContext, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    baseContext, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        "BODY_SENSORS",
                        "ACCESS_COARSE_LOCATION",
                        "ACCESS_FINE_LOCATION"
                    ),
                    0
                )
            }
        }

        setAlarm()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
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
            if (destination.id == R.id.navigation_calendarActivity || destination.id == R.id.navigation_newActivity || destination.id == R.id.navigation_activityRecord) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setAlarm() {
        val mAlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val calendar1: Calendar = Calendar.getInstance()
        calendar1.set(Calendar.HOUR_OF_DAY, 9)
        calendar1.set(Calendar.MINUTE, 0)

        // REMIND_TRACKING
        val intent1 = Intent(this, AlarmReceiver::class.java)
        intent1.action = "REMIND_TRACKING"
        val pendingIntent1 = PendingIntent.getBroadcast(
            this, 123, intent1, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        mAlarmManager.cancel(pendingIntent1)
        mAlarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar1.getTimeInMillis()+5000,
            AlarmManager.INTERVAL_HALF_DAY,
            pendingIntent1
        )

        // REGISTER_NONE_ACTIVITY
        val calendar2: Calendar = Calendar.getInstance()
        calendar2.set(Calendar.HOUR_OF_DAY, 23)
        calendar2.set(Calendar.MINUTE, 55)

        val intent2 = Intent(this, AlarmReceiver::class.java)
        intent2.action = "REGISTER_NONE_ACTIVITY"
        val pendingIntent2 = PendingIntent.getBroadcast(
            this, 124, intent2, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        mAlarmManager.cancel(pendingIntent2)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (mAlarmManager.canScheduleExactAlarms()) mAlarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC, calendar2.getTimeInMillis(), pendingIntent2
            )
            else {
                val alarmPermissionResultLauncher: ActivityResultLauncher<Intent> =
                    registerForActivityResult(
                        ActivityResultContracts.StartActivityForResult()
                    ) {
                        if (it != null && it.resultCode == Activity.RESULT_OK) {
                            Log.d("AlarmManager", "SCHEDULE_EXACT_ALARM permission granted")
                            mAlarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pendingIntent2
                            )
                        } else Log.d("AlarmManager", "SCHEDULE_EXACT_ALARM permission denied")
                    }

                AlertDialog.Builder(this).setTitle("Alarm permission")
                    .setMessage("We need this permission to remind you to register activities")
                    .setPositiveButton("ALLOW") { _, _ ->
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.setData(uri)
                        alarmPermissionResultLauncher.launch(intent)
                    }.setNegativeButton("DENY") { _, _ ->
                        Log.i("AlarmManager", "SCHEDULE_EXACT_ALARM permission not granted")
                    }.setIcon(android.R.drawable.ic_dialog_info).show()
            }
        } else {
            mAlarmManager.setInexactRepeating(
                AlarmManager.RTC,
                calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent2
            )
        }
    }
}