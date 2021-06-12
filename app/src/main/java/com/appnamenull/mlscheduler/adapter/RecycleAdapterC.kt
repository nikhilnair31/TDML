package com.appnamenull.mlscheduler.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appnamenull.mlscheduler.R
import com.appnamenull.mlscheduler.model.Task
import java.util.*

class RecycleAdapterC(private val comptaskList: ArrayList<Task> ) : RecyclerView.Adapter<RecycleAdapterC.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textViewNameC : TextView = itemView.findViewById(R.id.listitemTaskNameC)
        val textViewDeetsC : TextView = itemView.findViewById(R.id.listitemTaskDeetsC)
        val textViewDateC : TextView = itemView.findViewById(R.id.listitemTaskDateC)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        println("C RECYCLERADAPTER onBindViewHolder")
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.comp_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = comptaskList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task : Task = comptaskList[position]
        println("C RECYCLERADAPTER onBindViewHolder : ${task.getCompStat()} - position : $position | taskList[position] : ${comptaskList[position]}")
        val taskname = task.getTask()
        val details = task.getDetails()
        val datetime = task.getDateTime()
        holder.textViewNameC.text = taskname
        holder.textViewDeetsC.text = details
        holder.textViewDateC.text = datetime
        holder.textViewNameC.paintFlags = holder.textViewNameC.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        if(task.getDetails().isNullOrEmpty())
            holder.textViewDeetsC.visibility = View.GONE
        if(task.getDateTime().isNullOrEmpty())
            holder.textViewDateC.visibility = View.GONE
    }

}