package com.appnamenull.mlscheduler.Utils

import android.content.Context
import com.appnamenull.mlscheduler.model.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Cloud(val context: Context) {

    private val uidPreferences by lazy { context.getSharedPreferences("useruid", Context.MODE_PRIVATE) }
    private val childStr = uidPreferences.getString("useruid","").toString()
    private val ref = FirebaseDatabase.getInstance().reference.child( childStr )

    fun pushAllTasks(taskList: List<Task>) {
        if (taskList.isNotEmpty()) {
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for(i in taskList.indices) {
                        println("CLOUD pushTasks task : ${taskList[i]}\ttaskid : ${taskList[i].getId()}\ttaskname : ${taskList[i].getTask()}\ttaskdeets: ${taskList[i].getDetails()}\ttaskdate : ${taskList[i].getDateTime()}\t")
                        dataSnapshot.ref.child(taskList[i].getId().toString()).setValue(taskList[i])
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    fun pushTasks(taskList: List<Task>, id : Int) {
        if (taskList.isNotEmpty()) {
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    println("CLOUD pushTasks task : ${taskList[0]}\ttaskid : ${taskList[0].getId()}\ttaskname : ${taskList[0].getTask()}\ttaskdeets: ${taskList[0].getDetails()}\ttaskdate : ${taskList[0].getDateTime()}\t")
                    dataSnapshot.ref.child(id.toString()).setValue(taskList[0])
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun updateTasks(task: Task) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("CLOUD updateTasks task : ${task}\ttaskid : ${task.getId()}\ttaskname : ${task.getTask()}\ttaskdeets: ${task.getDetails()}\ttaskdate : ${task.getDateTime()}\t")
                dataSnapshot.ref.child(task.getId().toString()).setValue(task)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    interface OnGetDataListener {
        fun onStart()
        fun onSuccess(taskList: List<Task>?)
        fun onFailed(databaseError: DatabaseError?)
    }

    fun mReadDataOnce(listener: OnGetDataListener) {
        val taskList: ArrayList<Task> = ArrayList()
        listener.onStart()
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("CLOUD mReadDataOnce addListenerForSingleValueEvent dataSnapshot : $dataSnapshot")
                for (data in dataSnapshot.children) {
                    println("CLOUD mReadDataOnce data : $data")
                    if(data.child("delStat").value == "NOT DEL") {
                        val task = Task()
                        task.setTask(data.child("task").value as String?)
                        task.setDetails(data.child("details").value as String?)
                        task.setDateTime(data.child("dateTime").value as String?)
                        task.setCompStat(data.child("compStat").value as String?)
                        task.setDelStat(data.child("delStat").value as String?)
                        taskList.add(task)
                    }
                }
                listener.onSuccess(taskList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                listener.onFailed(databaseError)
            }
        })
    }

    fun retrieveTasks() : List<Task> {
        val taskList: ArrayList<Task> = ArrayList()
        println("CLOUD retrieveTasks before addListenerForSingleValueEvent")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("CLOUD retrieveTasks dataSnapshot : $dataSnapshot")
                for (data in dataSnapshot.children) {
                    val task = Task()
                    val temp = data.child("id").value
                    //task.setId(data.child("id").value as Int)
                    task.setTask(data.child("task").value as String?)
                    task.setDetails(data.child("details").value as String?)
                    task.setDateTime(data.child("datetime").value as String?)
                    taskList.add(task)
                    println("CLOUD retrieveTasks task : ${task.getTask()}\tdetails : ${task.getDetails()}\tdatetime : ${task.getDateTime()}\t")
                }
                println("CLOUD retrieveTasks post loop taskList : $taskList")
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        println("CLOUD retrieveTasks after addListenerForSingleValueEvent")
        return taskList
    }

    fun updateSpecificTask(task: Task, id: Int) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("CLOUD updateTasks task : ${task}\ttaskid : ${task.getId()}\ttaskname : ${task.getTask()}\ttaskdeets: ${task.getDetails()}\t" +
                        "taskdate : ${task.getDateTime()}\ttaskcomp : ${task.getCompStat()}\ttaskdel : ${task.getDelStat()}\t")
                dataSnapshot.ref.child(id.toString()).setValue(task)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun deleteSpecificTasks(id: Int){
        ref.child(id.toString()).removeValue()
    }

    fun deleteTaskbyDelStat(id: Int){
        //ref.child(id.toString()).removeValue()
    }

    fun deleteAllTasks(){
        println("CLOUD deleteAllTasks")
        ref.removeValue()
    }

}