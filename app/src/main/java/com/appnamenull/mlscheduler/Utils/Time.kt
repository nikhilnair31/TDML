package com.appnamenull.mlscheduler.Utils

import android.content.Context
import android.widget.Toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToLong

object Time {

    private val formatter: DateFormat = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.US)

    fun hourRound(d: Double, t: Float): Double{
        return ((t * d).roundToLong()/d)
    }

    fun minCalc(hr: Double): Int{
        var minRounded = hr - floor(hr)
        minRounded = ((minRounded * 100.0).roundToLong()/100.0)*60.0
        return minRounded.toInt()
    }

    private fun getDate(millis : Long, calendar : Calendar, context : Context){
        calendar.timeInMillis = millis
        val time = formatter.format(calendar.time)
        println("Date : $time")
        Toast.makeText(context, "millis : $time", Toast.LENGTH_SHORT).show()
    }

    fun setCal(daysFromToday : Int, context : Context): Calendar{
        val calendar: Calendar = Calendar.getInstance()
        //println("calender0 :${calendar}")
        //println("calender1 :${formatter.format(calendar.time)}")
        if(daysFromToday == 0){
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
        else{
            calendar[Calendar.HOUR_OF_DAY] = (24*daysFromToday)
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
        //println("calender2 :${formatter.format(calendar.time)}")
        return calendar
    }
}