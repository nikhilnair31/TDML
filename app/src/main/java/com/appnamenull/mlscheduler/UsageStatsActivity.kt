package com.appnamenull.mlscheduler

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_usage_stats.*
import java.text.SimpleDateFormat
import java.util.*

class UsageStatsActivity : AppCompatActivity() {

    lateinit var usagestatsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_stats)
        usagestatsText = findViewById(R.id.usagestatsText)
        if(checkUsageStatePermission()){
            showUsageStats()
        }
        else{
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    fun showUsageStats(){
        val usageStatsManager: UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val cal: Calendar = Calendar.getInstance()
        val time : String = SimpleDateFormat("yyyy-MM-dd hh:mm").format(cal.time);
        println("Date and time1 : \n$time\n\n")
        cal.add(Calendar.DAY_OF_MONTH, -1)
        val time1 : String = SimpleDateFormat("yyyy-MM-dd hh:mm").format(cal.time);
        println("Date and time2 : \n$time1\n\n")
        val currentTime = System.currentTimeMillis()
        val beginTime = cal.timeInMillis
        println("curr time : \n" +Date(currentTime) +"\t"+ Date(beginTime))
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
        val appOpsManager: AppOpsManager?
        val mode: Int
        appOpsManager = getSystemService(Context.APP_OPS_SERVICE) !! as AppOpsManager
        mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), applicationContext.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
