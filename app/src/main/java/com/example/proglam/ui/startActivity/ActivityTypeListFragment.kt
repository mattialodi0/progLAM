package com.example.proglam.ui.startActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proglam.R
import com.example.proglam.db.ActivityTypeViewModel

class ActivityTypeListFragment: Fragment() {

    private lateinit var mActivityTypeViewModel: ActivityTypeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_activitytype_list, container, false)

        return view
    }
}