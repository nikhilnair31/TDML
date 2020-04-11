package com.appnamenull.mlscheduler.Utils

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.RemoteException
import android.widget.TextView
import android.widget.Toast
import com.appnamenull.mlscheduler.AppUsageInfo
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


object UsageStatsObj {

    private val datehourformatter: DateFormat = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.US)
    private val dateformatter: DateFormat = SimpleDateFormat("dd/MM/yy", Locale.US)
    private val hourformatter: DateFormat = SimpleDateFormat("hh:mm a", Locale.US)

    private val appTotalTimeList = mutableListOf<Int>()
    private val timeSlotList = mutableListOf<String>()
    private val totalTxBytesWifi = mutableListOf<Long>()

    fun getTotalUsageOverTime(context : Context, mUsageStatsManager : UsageStatsManager, networkStatsManager : NetworkStatsManager, usagestatsText : TextView, txtDate : TextView, daysFromToday : Int) {
        usagestatsText.text = ""
        txtDate.text = ""
        appTotalTimeList.clear()
        timeSlotList.clear()
        totalTxBytesWifi.clear()

        val cal = Time.setCal(daysFromToday, context)
        //println("calender3 :${dateformatter.format(cal.time)}")
        txtDate.text = dateformatter.format(cal.time)
        var starttime = cal.timeInMillis
        var endtime = starttime
        for(i in 0 until 24) {
            starttime = endtime
            endtime = starttime + (1000 * 60 * 60.toLong())
            timeSlotList.add(getHours(starttime)+ " to " +getHours(endtime))
            totalTxBytesWifi(networkStatsManager, starttime, endtime)
            setUsageInfo(context, mUsageStatsManager, starttime, endtime)
            //println("endtime :$endtime\tdate : ${getHours(endtime)}\nstarttime : $starttime\tdate : ${getHours(starttime)}")
        }
        var str : String = ""
        for(i in 0 until 24) {
            str += timeSlotList[i]+" : "+appTotalTimeList[i]+" min "+(totalTxBytesWifi[i]/1000000)+" MB\n"
            println("1i :$i\ttimeSlotList :${timeSlotList[i]}\tappTotalTimeList : ${appTotalTimeList[i]}\ttotalTxBytesWifi : ${totalTxBytesWifi[i]}\n")
        }
        usagestatsText.text = str
        for(i in 0 until 24) {
            println("2i :$i\ttimeSlotList :${timeSlotList[i]}\tappTotalTimeList : ${appTotalTimeList[i]}\ttotalTxBytesWifi : ${totalTxBytesWifi[i]/1000000}\n")
        }
        println("3i timeSlotList :${timeSlotList}\nappTotalTimeList : ${appTotalTimeList}\ntotalTxBytesWifi : ${totalTxBytesWifi.size}\n")
    }

    private fun setUsageInfo(context: Context, mUsageStatsManager : UsageStatsManager, starting_time : Long, end_time : Long){
        var currentEvent: UsageEvents.Event
        val allEvents: MutableList<UsageEvents.Event> = ArrayList()
        val map: HashMap<String, AppUsageInfo> = HashMap()

        if (mUsageStatsManager != null) {
            val usageEvents = mUsageStatsManager.queryEvents(starting_time, end_time)
            while (usageEvents.hasNextEvent()) {
                currentEvent = UsageEvents.Event()
                usageEvents.getNextEvent(currentEvent)
                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED ) {
                    allEvents.add(currentEvent)
                    val key = currentEvent.packageName
                    if (map[key] == null)
                        map[key] = AppUsageInfo(key)
                }
            }
            // Traverse through each app data and count launch, calculate duration
            for (i in 0 until allEvents.size - 1) {
                val event0 = allEvents[i]
                val event1 = allEvents[i + 1]
//                if (event0.packageName != event1.packageName && event1.eventType == 1) {
//                    map[event1.packageName]!!.launchCount++
//                }
                if (event0.eventType == 1 && event1.eventType == 2 && event0.className == event1.className ) {
                    val diff = event1.timeStamp - event0.timeStamp
                    map[event0.packageName]?.timeInForeground = map[event0.packageName]?.timeInForeground?.plus(diff)!!
                }
            }
            val smallInfoList = ArrayList(map.values)
            var hourUse : Float = 0.0F
            for (appUsageInfo in smallInfoList) {
                hourUse += (appUsageInfo.timeInForeground.toFloat()/60000)
            }
            appTotalTimeList.add(hourUse.toInt())
        }
        else {
            Toast.makeText(context, "Sorry...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun totalTxBytesWifi(networkStatsManager : NetworkStatsManager, starting_time : Long, end_time : Long) : Long{
        val bucket: NetworkStats.Bucket = try {
            networkStatsManager.querySummaryForDevice( ConnectivityManager.TYPE_WIFI, "", starting_time, end_time )
        }
        catch (e: RemoteException) {
            return -1
        }
        totalTxBytesWifi.add(bucket.txBytes)
        return bucket.txBytes
    }

    private fun getHours(millis : Long): String{
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return hourformatter.format(calendar.time)
    }
}