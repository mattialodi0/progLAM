package com.example.proglam.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proglam.R
import com.example.proglam.db.ActivityType

class ActivityTypeListAdapter: RecyclerView.Adapter<ActivityTypeListAdapter.MyViewHolder>() {

    private var activityTypeList = emptyList<ActivityType>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.activitytype_button_layout, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = activityTypeList[position]
        //holder.itemView.name.text = currentItem.name
        //holder.itemView.icon.srcCompat = "@drawable/"+currentItem.iconSrc
    }

    override fun getItemCount(): Int {
        return activityTypeList.size
    }

    fun setData(activityType: List<ActivityType>) {
        this.activityTypeList = activityType
        notifyDataSetChanged()
    }
}