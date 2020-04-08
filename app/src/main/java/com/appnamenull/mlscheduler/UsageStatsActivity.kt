package com.appnamenull.mlscheduler

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.appnamenull.mlscheduler.Utils.StatsCalc


class UsageStatsActivity : AppCompatActivity() {

    lateinit var usagestatsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_stats)
        usagestatsText = findViewById(R.id.usagestatsText)
        if(checkUsageStatePermission()){
            showUsageStatsx()
        }
        else{
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            Toast.makeText(this, "Need to request permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showUsageStatsx(){
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        var startMillis: Long = 0
        val endMillis = System.currentTimeMillis()
        val endresultdate = Date(System.currentTimeMillis())
        calendar[Calendar.DAY_OF_MONTH] = 1
        startMillis = calendar.timeInMillis
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm")
        val startresultdate = Date(startMillis)
        println("From " + sdf.format(startresultdate))
        println("To " + sdf.format(endresultdate))
        val appPkg : String = "com.instagram.android"
        val lUsageStatsMap: Map<String, UsageStats> = StatsCalc.getUsageStatsManager1()!!.queryAndAggregateUsageStats(startMillis, endMillis)
        var total = 0.0f
        if (lUsageStatsMap.containsKey(appPkg)) {
            val seconds: Float = lUsageStatsMap[appPkg]!!.totalTimeInForeground / 1000.toFloat()
            val minutes = seconds / 60
            val hours = minutes / 60
            total = hours
        }
        usagestatsText.text = total.toString()
    }

    fun showUsageStats(){
        val usageStatsManager: UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val time : String = SimpleDateFormat("yyyy-MM-dd hh:mm").format(cal.time);
        cal.add(Calendar.DAY_OF_MONTH, -1)
        val time1 : String = SimpleDateFormat("yyyy-MM-dd hh:mm").format(cal.time);
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val beginTime = cal.getTimeInMillis()
        val currentTime = System.currentTimeMillis()
        val queryUsageStats : List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, currentTime)
        var statsdata = ""
        for(i in queryUsageStats.indices){
                statsdata = statsdata + "Package Name : " + queryUsageStats[i].packageName + "\nLast Time Used : " + convertTime(queryUsageStats[i].lastTimeUsed) +
                    "\nFirst Time Stamp : " + convertTime(queryUsageStats[i].firstTimeStamp) +
                            "\nLast Time Stamp : " + convertTime(queryUsageStats[i].lastTimeStamp) +
                                "\nTotal Time in FG : " + convertTime2(queryUsageStats[i].totalTimeInForeground) + "\n\n"
        }
        usagestatsText.text = statsdata
    }

    private  fun  convertTime(lastTimeUsed: Long): String{
        val date: Date = Date(lastTimeUsed)
        val format : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
        return format.format(date)
    }

    private  fun  convertTime2(lastTimeUsed: Long): String{
        val date = Date(lastTimeUsed)
        val format = SimpleDateFormat("hh:mm", Locale.ENGLISH)
        return format.format(date)
    }

    private fun checkUsageStatePermission() : Boolean{
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow( AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
