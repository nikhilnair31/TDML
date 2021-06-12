package com.appnamenull.mlscheduler.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appnamenull.mlscheduler.R
import com.appnamenull.mlscheduler.SingleTaskActivity
import com.appnamenull.mlscheduler.Utils.Forecasting
import com.appnamenull.mlscheduler.Utils.SavingSQLUsage
import com.appnamenull.mlscheduler.model.Task
import java.text.SimpleDateFormat
import java.util.*


class RecycleAdapter(private val taskList: ArrayList<Task> ) : RecyclerView.Adapter<RecycleAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    class ViewHolder(itemView:  View, context: Context): RecyclerView.ViewHolder(itemView){
        val textViewName : TextView = itemView.findViewById(R.id.listitemTaskName)
        val textViewDeets : TextView = itemView.findViewById(R.id.listitemTaskDeets)
        val textViewDate : TextView = itemView.findViewById(R.id.listitemTaskDate)
        val textViewPred : TextView = itemView.findViewById(R.id.listitemTaskPred)
        val con = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        println("RECYCLERADAPTER onBindViewHolder")
        val c: Context = parent.context
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(v, c)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task : Task = taskList[position]

        println("RECYCLERADAPTER onBindViewHolder : ${task.getCompStat()} - position : $position | taskList[position] : ${taskList[position]}")
        if(task.getCompStat() == "NOT COMP"){
            val taskname = task.getTask()
            val details = task.getDetails()
            val datetime = task.getDateTime()
            holder.textViewName.text = taskname
            holder.textViewDeets.text = details
            holder.textViewDate.text = datetime

            var pred = ""
            var maxIdx = 0
            val mydb = SavingSQLUsage(holder.con)
            if(!datetime.isNullOrEmpty()) {
                val taskdate = SimpleDateFormat("dd.MM.yyyy\t\thh:mm a", Locale.ENGLISH).parse(datetime).time
                val todate: Long = System.currentTimeMillis()
                val diff: Long = taskdate - todate
                val hour = (diff / (1000 * 60 * 60)).toInt()
                println("$taskdate\t-\t$todate : $diff = $hour")
                if (hour > 0) {
                    val forecastList = Forecasting.arima(mydb.getUsageTillNow(), hour)
                    val maxes = arrayListOf<Double>()
                    for (i in 1 until forecastList.size-1) {
                        println("RECYCLERADAPTER onBindViewHolder : ${forecastList[i-1]} | ${forecastList[i]} | ${forecastList[i+1]}")
                        if (forecastList[i-1] < forecastList[i] && forecastList[i] > forecastList[i+1]){
                            maxes.add(i.toDouble())
                            break
                        }
                        println("RECYCLERADAPTER onBindViewHolder maxes : $maxes")
                    }
                    val max = forecastList.max()
                    println("RECYCLERADAPTER onBindViewHolder max : $max")
                    if(maxes.isNullOrEmpty() && max != null)
                        maxIdx = max.toInt()
                    else
                        maxIdx = maxes[0].toInt()
                    pred = "Free in ${maxIdx + 1} hours"
                    holder.textViewPred.text = pred
                    println("RECYCLERADAPTER onBindViewHolder maxes : $maxes\tmaxIdx : $maxIdx")
                }
                else {
                    pred = "Maybe do it now?"
                    holder.textViewPred.text = pred
                }
            }

            if(task.getDetails().isNullOrEmpty())
                holder.textViewDeets.visibility = View.GONE
            if(task.getDateTime().isNullOrEmpty()){
                holder.textViewDate.visibility = View.GONE
                holder.textViewPred.visibility = View.GONE
            }

            holder.textViewName.setOnClickListener { holder ->
                val intent = Intent(holder.context, SingleTaskActivity::class.java)
                putExtraData(intent, task, pred)
                holder.context.startActivity(intent)
            }
            holder.textViewDeets.setOnClickListener { holder ->
                val intent = Intent(holder.context, SingleTaskActivity::class.java)
                putExtraData(intent, task, pred)
                holder.context.startActivity(intent)
            }
            holder.textViewDate.setOnClickListener { holder ->
                val intent = Intent(holder.context, SingleTaskActivity::class.java)
                putExtraData(intent, task, pred)
                holder.context.startActivity(intent)
            }
            holder.textViewPred.setOnClickListener { holder ->
                val intent = Intent(holder.context, SingleTaskActivity::class.java)
                putExtraData(intent, task, pred)
                holder.context.startActivity(intent)
            }
        }
    }

    private fun putExtraData(intent : Intent, task : Task, pred : String){
        println("RECYCLERADAPTER onBindViewHolder\tid : ${task.getId()}\ttaskname : ${task.getTask()}\ttaskdeets : ${task.getDetails()}\ttaskdate : ${task.getDateTime()}")
        intent.putExtra("taskid", task.getId())
        intent.putExtra("taskname", task.getTask())
        intent.putExtra("taskdeets", task.getDetails())
        intent.putExtra("taskdate", task.getDateTime())
        intent.putExtra("taskpred", pred)
        intent.putExtra("task", task)
    }
}