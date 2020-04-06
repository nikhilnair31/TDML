package com.appnamenull.mlscheduler

import android.app.Activity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.TextView
import java.io.Console
import java.text.SimpleDateFormat
import java.util.*

import com.appnamenull.mlscheduler.R.id.item1
import com.appnamenull.mlscheduler.R.id.item2
import com.appnamenull.mlscheduler.R.id.item3
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_drawer.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //private lateinit var tvUsageStats : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        init()
        if(checkUsageStatePermission()){
            println()
        }
        else{
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private  fun init(){
        val toggle = ActionBarDrawerToggle(Activity(), drawer_layout, toolbar, R.string.navopen, R.string.navclose)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        navigation_view.setNavigationItemSelectedListener(this)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            item1 ->{
                message.text = item.title
            }
            item2 ->{
                message.text = item.title
            }
            item3 ->{
                val usageStatsManager: UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val cal: Calendar = Calendar.getInstance()
                cal.add(Calendar.YEAR, -2)
                val currentTime = System.currentTimeMillis()
                val beginTime = cal.timeInMillis
                val queryUsageStats : List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, currentTime)
                var statsdata = ""
                for(i in queryUsageStats.indices){
                    var testStr = convertTime(queryUsageStats[i].lastTimeUsed)
                    if(testStr.substring(6,10) != "1970") {
                        //println(testStr+"\t"+testStr.substring(6,10))
                        statsdata = statsdata + "Package Name : " + queryUsageStats[i].packageName + "\n" +
                                "Last Time Used : " + convertTime(queryUsageStats[i].lastTimeUsed) + "\n" +
                                "First Time Stamp : " + convertTime(queryUsageStats[i].firstTimeStamp) + "\n" +
                                "Last Time Stamp : " + convertTime(queryUsageStats[i].lastTimeStamp) + "\n" +
                                "Total Time in FG : " + convertTime2(queryUsageStats[i].totalTimeInForeground) + "\n\n"
                    }
                }
                message.text = statsdata
            }
            else ->{

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private  fun  convertTime(lastTimeUsed: Long): String{
        val date: Date = Date(lastTimeUsed)
        val format : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
        return format.format(date)
    }

    private  fun  convertTime2(lastTimeUsed: Long): String{
        val date: Date = Date(lastTimeUsed)
        val format : SimpleDateFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
        return format.format(date)
    }

    private fun checkUsageStatePermission() : Boolean{
        var appOpsManager: AppOpsManager ?= null
        var mode = 0
        appOpsManager = getSystemService(Context.APP_OPS_SERVICE) !! as AppOpsManager
        mode = appOpsManager.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), applicationContext.packageName)
        return mode == MODE_ALLOWED
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            //R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
