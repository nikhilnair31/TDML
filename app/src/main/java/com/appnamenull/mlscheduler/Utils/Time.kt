package com.appnamenull.mlscheduler.Utils

import android.content.Context
import android.widget.Toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToLong

object Time {

    private var startMillis: Long = 0
    private var endMillis = System.currentTimeMillis()

    fun hourRound(d: Double, t: Float): Double{
        return ((t * d).roundToLong()/d)
    }

    fun minCalc(hr: Double): Int{
        var minRounded = hr - floor(hr)
        minRounded = ((minRounded * 100.0).roundToLong()/100.0)*60.0
        return minRounded.toInt()
    }

    private fun getDate(millis : Long, calendar : Calendar, context : Context){
        val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US)
        calendar.timeInMillis = millis
        val time = formatter.format(calendar.time)
        println("Date : $time")
        Toast.makeText(context, "millis : $time", Toast.LENGTH_SHORT).show()
    }

    fun setDuration(selectedPeriod : String, context : Context): MutableList<Long>{
        val calendar: Calendar = Calendar.getInstance()
        when (selectedPeriod) {
            "TODAY" -> {
                startMillis = 0
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
                startMillis = calendar.timeInMillis
                endMillis = System.currentTimeMillis()
            }
            "YESTERDAY" -> {
                calendar[Calendar.HOUR_OF_DAY] = -24
                startMillis = calendar.timeInMillis
                calendar[Calendar.HOUR_OF_DAY] = 23
                calendar[Calendar.MINUTE] = 59
                calendar[Calendar.SECOND] = 59
                calendar[Calendar.MILLISECOND] = 99
                endMillis = calendar.timeInMillis
            }
            "WEEKLY" -> {
                calendar[Calendar.DAY_OF_WEEK] = calendar.firstDayOfWeek
                startMillis = calendar.timeInMillis
                endMillis = System.currentTimeMillis()
            }
            "MONTHLY" -> {
                calendar[Calendar.DAY_OF_MONTH] = 1
                startMillis = calendar.timeInMillis
                endMillis = System.currentTimeMillis()
            }
            else -> {
            }
        }
        getDate(startMillis, calendar, context)
        getDate(endMillis, calendar, context)
        val milList = mutableListOf<Long>()
        milList.add(startMillis)
        milList.add(endMillis)
        return milList
    }

    fun setDuration1(time : Int, selectedPeriod : String, context : Context): MutableList<Long>{
        val calendar: Calendar = Calendar.getInstance()
        val calendar1: Calendar = Calendar.getInstance()
        when (selectedPeriod) {
            "TODAY" -> {
                startMillis = 0
                calendar[Calendar.HOUR_OF_DAY] = time - 1
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
                calendar1[Calendar.HOUR_OF_DAY] = time
                calendar1[Calendar.MINUTE] = 0
                calendar1[Calendar.SECOND] = 0
                calendar1[Calendar.MILLISECOND] = 0
                startMillis = calendar.timeInMillis
                endMillis = calendar1.timeInMillis
            }
            else -> {
            }
        }
        getDate(startMillis, calendar, context)
        getDate(endMillis, calendar, context)
        val milList = mutableListOf<Long>()
        milList.add(startMillis)
        milList.add(endMillis)
        return milList
    }
}