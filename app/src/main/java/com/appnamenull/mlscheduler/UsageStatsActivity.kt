package com.appnamenull.mlscheduler

import android.app.usage.UsageStats
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnamenull.mlscheduler.Utils.Packages
import com.appnamenull.mlscheduler.Utils.Permission
import com.appnamenull.mlscheduler.Utils.StatsCalc
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToLong


class UsageStatsActivity : AppCompatActivity() {

    lateinit var usagestatsText: TextView
    lateinit var fabRefresh: FloatingActionButton

    private var total = 0.0f
    private var hourRounded : Double = 0.0
    private var minRounded : Double = 0.0
    private var startMillis: Long = 0
    private var timepack : String = ""
    private val defaultList = arrayOf("com.instagram.android","com.whatsapp","com.android.chrome","com.twitter.android","com.notdoppler.crashofcars")

    private val calendar: Calendar = Calendar.getInstance()
    private val endMillis = System.currentTimeMillis()
    private val endresultdate = Date(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_stats)
        fabRefresh = findViewById(R.id.fabRefresh)
        usagestatsText = findViewById(R.id.usagestatsText)

        if(Permission.checkUsageStatePermission(applicationContext)){
            StatsCalc.initAppHelper(applicationContext)
            showUsageStatsx()
        }
        else{
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            Toast.makeText(this, "Need to request permission", Toast.LENGTH_LONG).show()
        }
    }

    fun onRefreshPressed(view: View?) {
        timepack = ""
        usagestatsText.text = timepack
        StatsCalc.initAppHelper(applicationContext)
        showUsageStatsx()
        Toast.makeText(this, "Refreshed", Toast.LENGTH_LONG).show()
    }

    private fun setTimetoMonthStart(){
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.DAY_OF_MONTH] = 1
        startMillis = calendar.timeInMillis
    }

    private fun showUsageStatsx(){
        setTimetoMonthStart()
        val startresultdate = Date(startMillis)
        val endresultdate = Date(System.currentTimeMillis())
        val usageStatsMap: Map<String, UsageStats> = StatsCalc.getUsageStatsManager1().queryAndAggregateUsageStats(startMillis, endMillis)
        val appList : MutableList<String> = Packages.getAllPackages(applicationContext)
        val appTimeList = mutableListOf<String>()
        for (element in appList){
            val appPkg: String = element
            if (usageStatsMap.containsKey(appPkg)) {
                val seconds: Float = usageStatsMap[appPkg]!!.totalTimeInForeground / 1000.toFloat()
                val minutes = seconds / 60
                val hours = minutes / 60
                total = hours
                hourRound(100.0)
                minCalc()
                appTimeList.add("${floor(hourRounded).toInt()}:${minRounded.toInt()}")
//                if(total > 0)
//                    timepack += ("$appPkg :\t${floor(hourRounded).toInt()} hours ${minRounded.toInt()} minutes\n")
            }
        }
        for (i in 0 until appList.size){
            if(appTimeList[i] != "0:0")
                timepack += appList[i]+" - "+appTimeList[i]+"\n"
        }
        usagestatsText.text = timepack
    }

    private fun hourRound(d: Double){
        hourRounded = ((total * d).roundToLong()/d)
    }

    private fun minCalc(){
        minRounded = hourRounded- floor(hourRounded)
        minRounded = ((minRounded * 100.0).roundToLong()/100.0)*60.0
    }

}
