package com.appnamenull.mlscheduler

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnamenull.mlscheduler.Utils.*
import com.appnamenull.mlscheduler.Utils.AppUsageInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class UsageStatsActivity : AppCompatActivity() {

    private lateinit var usagestatsText: TextView
    private lateinit var fabRefresh: FloatingActionButton

    private var timeRange : String = "TODAY"
    private val appTotalTimeList = mutableListOf<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_usage_stats)
        fabRefresh = findViewById(R.id.fabPush)
        usagestatsText = findViewById(R.id.usagestatsText)

        if(Permission.checkUsageStatePermission(applicationContext)){
        }
        else{
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            Toast.makeText(this, "Need to request permission", Toast.LENGTH_LONG).show()
        }
    }

    fun onPushPressed(view : View) {
        Cloud.pushString(usagestatsText.text as String)
    }

    fun onTodayPressed1(view : View) {
        usagestatsText.text = ""
        timeRange = "TODAY"
        val hourinmil = 1000 * 60 * 60.toLong()
        val endtime = System.currentTimeMillis()
        val starttime = endtime - hourinmil
        setUsageInfo2(starttime, endtime)
    }

    fun onTodayPressed(view : View) {
        usagestatsText.text = ""
        timeRange = "TODAY"
        //for(i in 1 until 24) {
        //   println("i : $i")
            Permission.initAppHelper(applicationContext)
            val milList: MutableList<Long> = Time.setDuration(timeRange, this)
            println("milList : $milList")
            setUsageInfo(milList[0], milList[1])
        //}
        println("appTotalTimeList : $appTotalTimeList")
    }

    fun onYesterdayPressed(view : View) {
        usagestatsText.text = ""
        timeRange = "YESTERDAY"
        Permission.initAppHelper(applicationContext)
        val milList: MutableList<Long> = Time.setDuration(timeRange, this)
        println("milList : $milList")
        setUsageInfo(milList[0], milList[1])
        println("appTotalTimeList : $appTotalTimeList")
    }

    fun onWeekPressed(view : View) {
        usagestatsText.text = ""
        timeRange = "WEEKLY"
        Permission.initAppHelper(applicationContext)
        val milList: MutableList<Long> = Time.setDuration(timeRange, this)
        setUsageInfo(milList[0], milList[1])
    }

    fun onMonthPressed(view : View) {
        usagestatsText.text = ""
        timeRange = "MONTHLY"
        Permission.initAppHelper(applicationContext)
        val milList: MutableList<Long> = Time.setDuration(timeRange, this)
        setUsageInfo(milList[0], milList[1])
    }

    private fun setUsageInfo(startMillis : Long, endMillis : Long){
        var str : String = ""
        val usageStatsMap: Map<String, UsageStats> = Permission.getUsageStatsManager1().queryAndAggregateUsageStats(startMillis, endMillis)
        val appPackList : MutableList<String> = PackageandTimeList.getPackageList(applicationContext)
        val appNameList : MutableList<String> = PackageandTimeList.packagetoName(applicationContext, appPackList)
        val appTimeList : MutableList<String> = PackageandTimeList.getTimeList(appPackList, usageStatsMap)
        var timeOfHour = PackageandTimeList.getTotalTimeList(appPackList, usageStatsMap)
        val appTotalTimeList = mutableListOf<Float>()
        appTotalTimeList.add(timeOfHour)
        for(word in getTimenPackage(appNameList, appTimeList)){
            //println("word : $word")
            str += word+"\n"
        }
        usagestatsText.text = str
    }

    private fun setUsageInfo1(startMillis : Long, endMillis : Long){
        val usageStatsMap: Map<String, UsageStats> = Permission.getUsageStatsManager1().queryAndAggregateUsageStats(startMillis, endMillis)
        val appPackList : MutableList<String> = PackageandTimeList.getPackageList(applicationContext)
        var timeOfHour = PackageandTimeList.getTotalTimeList(appPackList, usageStatsMap)
        appTotalTimeList.add(timeOfHour)
    }

    private fun setUsageInfo2(starting_time : Long, end_time : Long){
        var currentEvent: UsageEvents.Event
        val allEvents: MutableList<UsageEvents.Event> = ArrayList()
        val map: HashMap<String, AppUsageInfo> = HashMap()
        val mUsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        if (mUsageStatsManager != null) {
            // Get all apps data from starting time to end time
            val usageEvents = mUsageStatsManager.queryEvents(starting_time, end_time)
            // Put these data into the map
            while (usageEvents.hasNextEvent()) {
                currentEvent = UsageEvents.Event()
                usageEvents.getNextEvent(currentEvent)
                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED ) {
                    allEvents.add(currentEvent)
                    val key = currentEvent.packageName
                    if (map[key] == null) {
                        map[key] = AppUsageInfo(key)
                        println("key : $key\t\tmap[key] : ${map[key]}")
                    }
                }
            }
            //println("allEvents : $allEvents")
            //println("map : $map")
            // Traverse through each app data and count launch, calculate duration
            for (i in 0 until allEvents.size - 1) {
                val E0 = allEvents[i]
                val E1 = allEvents[i + 1]
                println("E0.packageName : ${E0.packageName}\t\tE1.packageName : ${E1.packageName}\t\tE1.eventType : ${E1.eventType}")
                if (E0.packageName != E1.packageName && E1.eventType == 1) {
                    map[E1.packageName]!!.launchCount++
                }
                if (E0.eventType == 1 && E1.eventType == 2 && E0.className == E1.className ) {
                    val diff = E1.timeStamp - E0.timeStamp
                    map[E0.packageName]?.timeInForeground =
                        map[E0.packageName]?.timeInForeground?.plus(diff)!!
                    println("map[E0.packageName].timeInForeground : ${map[E0.packageName]?.timeInForeground}\t\tdiff : $diff")
                }
            }
            val smallInfoList = ArrayList(map.values)
            //println("smallInfoList : $smallInfoList")
            // Concatenating data to show in a text view. You may do according to your requirement
            var strMsg : String = ""
            for (appUsageInfo in smallInfoList) {
                // Do according to your requirement
                strMsg += appUsageInfo.packageName.toString() + " : " + (appUsageInfo.timeInForeground/60000) + "\n\n"
                println("strMsg : $strMsg")
            }
            usagestatsText.text = strMsg
        } else {
            Toast.makeText(this, "Sorry...", Toast.LENGTH_SHORT).show()
        }
    }

    private  fun getTimenPackage(nameList : MutableList<String>, timeList : MutableList<String>) : Array<String> {
        var map: Map<String, String> = nameList.zip(timeList).toMap()
        map = map.filterValues { it != "0:0" }
        map = map.toSortedMap()
        return map.toString().split(",").toTypedArray()
    }

}
