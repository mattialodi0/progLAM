package com.example.proglam.background.activityServices

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.proglam.R
import com.example.proglam.ui.ongoingActivity.OngoingGpsActivity
import com.example.proglam.utils.DefaultLocationClient
import com.example.proglam.utils.LocationClient
import com.example.proglam.utils.Notifications
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


open class GpsService : BaseService() {
    companion object {
        private var _locations: ArrayList<LatLng> = arrayListOf()
        val locations: MutableLiveData<ArrayList<LatLng>> =
            MutableLiveData(arrayListOf(LatLng(0.0, 0.0)))
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun start() {
        super.start()
        locationClient
            .getLocationUpdates(5000L)
            .catch { e ->
                e.printStackTrace()
                abort(e)
            }
            .onEach { location ->
                if (_locations.isEmpty() || !(_locations.last().latitude == location.latitude && _locations.last().longitude == location.longitude)) {
                    _locations.add(LatLng(location.latitude, location.longitude))
                    locations.postValue(_locations)
                }
            }
            .launchIn(serviceScope)
    }

    override fun getNotificationBuilder(): NotificationCompat.Builder =
        NotificationCompat.Builder(this, Notifications.ONGOING_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_activitytype_generic)
            .setContentTitle("Tracking ${if (activityType != "") activityType else "an activity"}")
            .setContentText("00:00:00")
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    143,
                    Intent(this, OngoingGpsActivity::class.java).apply {
                        this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    },
                    PendingIntent.FLAG_MUTABLE
                )
            )
}