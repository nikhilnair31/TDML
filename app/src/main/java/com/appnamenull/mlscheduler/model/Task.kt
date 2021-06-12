package com.appnamenull.mlscheduler.model

import java.io.Serializable

class Task : Serializable {

    private var id = 0
    private var task: String? = null
    private var details: String? = null
    private var datetime: String? = null
    private var compstat: String? = null
    private var delstat: String? = null

    fun Task(id: Int, task: String?, details: String?, datetime: String?, compstat: String?, delstat: String?) {
        this.id = id
        this.task = task
        this.details = details
        this.datetime = datetime
        this.compstat = compstat
        this.delstat = delstat
    }

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getTask(): String? {
        return task
    }

    fun setTask(task: String?) {
        this.task = task
    }

    fun getDetails(): String? {
        return details
    }

    fun setDetails(details: String?) {
        this.details = details
    }

    fun getDateTime(): String? {
        return datetime
    }

    fun setDateTime(datetime: String?) {
        this.datetime = datetime
    }

    fun getCompStat(): String? {
        return compstat
    }

    fun setCompStat(compstat: String?) {
        this.compstat = compstat
    }

    fun getDelStat(): String? {
        return delstat
    }

    fun setDelStat(delstat: String?) {
        this.delstat = delstat
    }
}