package com.appnamenull.mlscheduler

import android.app.usage.NetworkStatsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnamenull.mlscheduler.Utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.appnamenull.mlscheduler.Utils.UsageStatsObj

internal class AppUsageInfo(var packageName: String) {
    var appIcon  : Drawable? = null
    var appName: String? = null
    var timeInForeground: Long = 0
    var launchCount = 0
}

class UsageStatsActivity : AppCompatActivity() {

    private lateinit var usagestatsText: TextView
    private lateinit var txtDate: TextView
    private lateinit var fabRefresh: FloatingActionButton

    private var daysFromToday : Int = 0

    private var mUsageStatsManager : UsageStatsManager? = null
    private var networkStatsManager : NetworkStatsManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_usage_stats)
        fabRefresh = findViewById(R.id.fabPush)
        usagestatsText = findViewById(R.id.usagestatsText)
        txtDate = findViewById(R.id.txtDate)
        mUsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        networkStatsManager = getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        if(Permission.checkUsageStatePermission(applicationContext)){
            UsageStatsObj.getTotalUsageOverTime(this,  mUsageStatsManager!!, networkStatsManager!!, usagestatsText, txtDate, daysFromToday)
        }
        else{
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            Toast.makeText(this, "Need to request permission", Toast.LENGTH_LONG).show()
        }
    }

    fun onPushPressed(view : View) {
        Cloud.pushString(usagestatsText.text as String)
    }

    fun onPushAdd(view : View) {
        if(daysFromToday == 0)
            Toast.makeText(this, "Can't predict the future, sorry.", Toast.LENGTH_LONG).show()
        else{
            daysFromToday++
            UsageStatsObj.getTotalUsageOverTime(this, mUsageStatsManager!!, networkStatsManager!!, usagestatsText, txtDate, daysFromToday)
        }
    }

    fun onPushSub(view : View) {
        if(daysFromToday == -10)
            Toast.makeText(this, "No more data, sorry.", Toast.LENGTH_LONG).show()
        else{
            daysFromToday--
            UsageStatsObj.getTotalUsageOverTime(this, mUsageStatsManager!!, networkStatsManager!!, usagestatsText, txtDate, daysFromToday)
        }
    }

}
