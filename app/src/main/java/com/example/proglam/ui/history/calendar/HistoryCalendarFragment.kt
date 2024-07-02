package com.example.proglam.ui.history.calendar

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proglam.R
import com.example.proglam.db.ActivityRecord
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.utils.Strings
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


class HistoryCalendarFragment : Fragment() {
    private lateinit var calendarView: CalendarView
    private lateinit var calendar: Calendar
    private val mActivityRecordViewModel: ActivityRecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history_calendar, container, false)

        setObservers(view)

        val time = Date().time
        val date = Date(time - time % (24 * 60 * 60 * 1000)).toInstant().toEpochMilli() - (2*60*60*1000)
        mActivityRecordViewModel.findActivitiesFromTo(date, date + 86400000L)

        return view
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
            val startTime = date

            clearActivities(view)
            if (!(date > System.currentTimeMillis())) {
                mActivityRecordViewModel.findActivitiesFromTo(startTime, startTime + 86400000L)
            }
        }))

        mActivityRecordViewModel.getActivitiesFromTo.observe(
            viewLifecycleOwner,
        ) { activityRecords ->
            displayActivities(view, activityRecords)
        }
    }

    private fun clearActivities(view: View) {
        val ll: LinearLayout = view.findViewById(R.id.historyActivityRecords_ll)
        ll.removeAllViews()
    }


    private fun displayActivities(view: View, activityRecords: List<ActivityRecord>) {
        var i = 0
        val dm = resources.displayMetrics
        val dpInPx16 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, dm).toInt()

        val ll: LinearLayout = view.findViewById(R.id.historyActivityRecords_ll)

        for (activityRecord in activityRecords) {
            if (i < activityRecords.size) {
                val arBtn = MaterialButton(requireContext())
                arBtn.cornerRadius = dpInPx16
                arBtn.setBackgroundColor(MaterialColors.getColor(
                    requireContext(),
                    R.attr.colorOnBackground,
                    Color.GRAY
                ))
                arBtn.text = "${activityRecord.type} - ${Strings.formattedTimer((activityRecord.finishTime-activityRecord.startTime) / 1000)}"
                if(com.example.proglam.utils.System.isNightModeOn(requireContext()))
                    arBtn.setTextColor(Color.GRAY)
                else
                    arBtn.setTextColor(Color.WHITE)
                arBtn.isAllCaps = false

                arBtn.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("arId", activityRecord.id.toString())
                    findNavController().navigate(R.id.navigation_activityRecord, bundle)
                }

                ll.addView(arBtn)
            }
            i++
        }
    }
}