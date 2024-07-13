package com.example.proglam.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.proglam.R
import com.example.proglam.db.ActivityType


@SuppressLint("NotifyDataSetChanged")
class ATRecyclerviewAdapter(
    context: Context,
    recyclerviewInterface: ATRecyclerviewInterface,
    liveDataToObserve: LiveData<List<ActivityType>>,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<ATRecyclerviewAdapter.MyViewHolder>() {
    private val atModels: ArrayList<ATModel> = ArrayList(5)
    private val recyclerviewInterface: ATRecyclerviewInterface?
    private val context: Context

    init {
        this.recyclerviewInterface = recyclerviewInterface
        this.context = context

        liveDataToObserve.observe(lifecycleOwner) { ats ->
            atModels.clear()
            if (ats != null && ats.isNotEmpty()) {
                for (a in ats) {
                    atModels.add(ATModel(a.name, a.iconSrc))
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
        val view = inflater.inflate(R.layout.recyclerview_col_at, parent, false)
        return MyViewHolder(view, recyclerviewInterface)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.atName.text = atModels[position].name
        holder.atIcon.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                context.resources.getIdentifier(
                    atModels[position].iconSrc,
                    "drawable",
                    context.packageName
                )
            )
        )
    }

    override fun getItemCount(): Int {
        return atModels.size
    }

    class MyViewHolder(view: View, recyclerviewInterface: ATRecyclerviewInterface?) :
        RecyclerView.ViewHolder(view) {
        var atName: TextView = view.findViewById(R.id.atName_tv)
        var atIcon: ImageView = view.findViewById(R.id.atIcon_iv)

        init {
            view.setOnClickListener {
                if (recyclerviewInterface != null) {
                    val pos = absoluteAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerviewInterface.onItemClick(pos)
                    }
                }
            }
            view.setOnLongClickListener {
                if (recyclerviewInterface != null) {
                    val pos = absoluteAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerviewInterface.onItemLongClick(pos)
                    }
                }
                return@setOnLongClickListener true
            }
        }
    }
}