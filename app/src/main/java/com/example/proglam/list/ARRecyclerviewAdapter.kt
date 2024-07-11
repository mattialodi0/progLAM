package com.example.proglam.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.proglam.R
import com.example.proglam.db.ActivityRecord
import com.example.proglam.utils.Strings


class ARRecyclerviewAdapter(
    recyclerviewInterface: ARRecyclerviewInterface,
    liveDataToObserve: LiveData<List<ActivityRecord>>,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<ARRecyclerviewAdapter.MyViewHolder>() {
    private val arModels: ArrayList<ARModel> = ArrayList(10)
    private val recyclerviewInterface: ARRecyclerviewInterface?

    init {
        this.recyclerviewInterface = recyclerviewInterface

        liveDataToObserve.observe(lifecycleOwner) {
            arModels.clear()
            if (it != null && it.isNotEmpty()) {
                for (ar in it) {
                    arModels.add(ARModel(ar.type, Strings.formattedTimer((ar.finishTime - ar.startTime) / 1000)))
                }
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyclerview_row_ar, parent, false)
        return MyViewHolder(view, recyclerviewInterface)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.arType.text = arModels[position].type
        holder.arDuration.text = arModels[position].duration.toString()
    }

    override fun getItemCount(): Int {
        return arModels.size
    }

    class MyViewHolder(view: View, recyclerviewInterface: ARRecyclerviewInterface?) : RecyclerView.ViewHolder(view) {
        var arType: TextView = view.findViewById(R.id.arType_tv)
        var arDuration: TextView = view.findViewById(R.id.arDuration_tv)
        init {
            view.setOnClickListener {
                if(recyclerviewInterface != null) {
                    val pos = absoluteAdapterPosition
                    if(pos != RecyclerView.NO_POSITION) {
                        recyclerviewInterface.onItemClick(pos)
                    }
                }
            }
        }
    }
}