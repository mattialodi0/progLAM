package com.example.proglam.ui.newActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.proglam.R
import com.example.proglam.databinding.FragmentNewActivityBinding
import com.example.proglam.db.ActivityType
import com.example.proglam.db.ActivityTypeViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial


class NewActivityFragment : Fragment() {

    private lateinit var mActivityTypeViewModel: ActivityTypeViewModel
    private lateinit var mNewActivityViewModel: NewActivityViewModel
    private lateinit var binding: FragmentNewActivityBinding
    private var tool1: Boolean = false
    private var tool2: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_activity, container, false)
        val view = binding.root

        mActivityTypeViewModel = ViewModelProvider(this)[ActivityTypeViewModel::class.java]

        val switch1 = view.findViewById<SwitchMaterial>(R.id.tools_switch1)
        val switch2 = view.findViewById<SwitchMaterial>(R.id.tools_switch2)
        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) tool1 = true
            else tool1 = false
            Log.w("aaa", tool1.toString())
        }
        switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) tool2 = true
            else tool1 = false
        }

        val addBtn: MaterialButton = view.findViewById(R.id.add_btn)
        addBtn.setOnClickListener {
            val tools = getTools(tool1, tool2)
            addActivityTypeToDB(mNewActivityViewModel.acName.value!!, mNewActivityViewModel.acDescription.value!!, tools)
            findNavController().popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        mNewActivityViewModel = ViewModelProvider(this)[NewActivityViewModel::class.java]
        binding.viewmodel = mNewActivityViewModel
    }

    private fun addActivityTypeToDB(name: String, desc: String, tools: Int) {
        if(name != "" && name != " ") {
            val activityType = ActivityType(0, name, desc, "ic_activitytype_generic", tools)
            mActivityTypeViewModel.addActivityType(activityType)
            Toast.makeText(requireContext(), "Added a new activity type", Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(requireContext(), "An activity type needs a name", Toast.LENGTH_SHORT).show()
    }

    private fun getTools(tool1: Boolean, tool2: Boolean): Int {
        if(tool1 && tool2)
            return 3
        else if(tool1 && !tool2)
            return 1
        else if(!tool1 && tool2)
            return 2
        else
            return 0
    }
}