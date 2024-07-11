package com.example.proglam.ui.history.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proglam.R
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.list.ARRecyclerviewAdapter
import com.example.proglam.list.ARRecyclerviewInterface
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


class HistoryCalendarFragment : Fragment(), ARRecyclerviewInterface {
    private val mActivityRecordViewModel: ActivityRecordViewModel by viewModels()
    private lateinit var calendarView: CalendarView
    private lateinit var calendar: Calendar
    private var calendarSavedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_calendar, container, false)

        setObservers(view)

        val time = Date().time
        val date = Date(time - time % (24 * 60 * 60 * 1000)).toInstant().toEpochMilli() - (2*60*60*1000)
        mActivityRecordViewModel.findActivitiesFromTo(date, date + 86400000L)

        val recyclerView = view.findViewById<RecyclerView>(R.id.historyAR_rv)
        val adapter = ARRecyclerviewAdapter(this, mActivityRecordViewModel.getActivitiesFromTo, viewLifecycleOwner)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        calendarView = view.findViewById(R.id.calendarView)
        if (savedInstanceState != null) {
            val timestamp = savedInstanceState.getString("calendar_saved_time")
            if(timestamp != null)
                calendarView.date = timestamp.toLong()
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("calendar_saved_time", calendarSavedTime.toString())
    }

    private fun setObservers(view: View) {
        calendarView = view.findViewById(R.id.calendarView)

        calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener(fun(
            _,
            year,
            month,
            day
        ) {
            val month = month + 1
            var monthStr = month.toString()
            var dayStr = day.toString()
            if (month < 10)
                monthStr = "0$month"
            if (day < 10)
                dayStr = "0$day"

            val l = LocalDate.parse(
                "$dayStr-$monthStr-$year",
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
            )
            val date = l.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond * 1000
            calendarSavedTime = date

            if (date <= System.currentTimeMillis()) {
                mActivityRecordViewModel.findActivitiesFromTo(date, date + 86400000L)
            }
        }))
    }

    override fun onItemClick(pos: Int) {
        val ars = mActivityRecordViewModel.getActivitiesFromTo.value
        if(ars != null) {
            val bundle = Bundle()
            bundle.putString("arId", ars[pos].id.toString())
            findNavController().navigate(R.id.navigation_activityRecord, bundle)
        }
    }
}