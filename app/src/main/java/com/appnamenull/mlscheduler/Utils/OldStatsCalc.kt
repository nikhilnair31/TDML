package com.appnamenull.mlscheduler.Utils

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object OldStatsCalc {

//    fun showUsageStats(context: Context){
//        val usageStatsManager: UsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        val cal: Calendar = Calendar.getInstance()
//        cal.set(Calendar.HOUR_OF_DAY, 0)
//        cal.set(Calendar.MINUTE, 0)
//        cal.set(Calendar.SECOND, 0)
//        cal.set(Calendar.MILLISECOND, 0)
//        val time : String = SimpleDateFormat("yyyy-MM-dd hh:mm").format(cal.time);
//        cal.add(Calendar.DAY_OF_MONTH, -1)
//        val time1 : String = SimpleDateFormat("yyyy-MM-dd hh:mm").format(cal.time);
//        cal.set(Calendar.DAY_OF_MONTH, 1)
//        val beginTime = cal.getTimeInMillis()
//        val currentTime = System.currentTimeMillis()
//        val queryUsageStats : List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, currentTime)
//        var statsdata = ""
//        for(i in queryUsageStats.indices){
//            statsdata = statsdata + "Package Name : " + queryUsageStats[i].packageName + "\nLast Time Used : " + convertTime(queryUsageStats[i].lastTimeUsed) +
//                    "\nFirst Time Stamp : " + convertTime(queryUsageStats[i].firstTimeStamp) +
//                    "\nLast Time Stamp : " + convertTime(queryUsageStats[i].lastTimeStamp) +
//                    "\nTotal Time in FG : " + convertTime2(queryUsageStats[i].totalTimeInForeground) + "\n\n"
//        }
//    }
//
//    private  fun  convertTime(lastTimeUsed: Long): String{
//        val date: Date = Date(lastTimeUsed)
//        val format : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
//        return format.format(date)
//    }
//
//    private  fun  convertTime2(lastTimeUsed: Long): String{
//        val date = Date(lastTimeUsed)
//        val format = SimpleDateFormat("hh:mm", Locale.ENGLISH)
//        return format.format(date)
//    }

}