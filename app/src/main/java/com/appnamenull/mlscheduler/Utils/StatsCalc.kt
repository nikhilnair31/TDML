package com.appnamenull.mlscheduler.Utils

import android.app.usage.UsageStatsManager
import android.content.Context

object StatsCalc {
    //const val TAG = "TIME_TRACKER"
    //const val APP_ID = "ca-app-pub-3912594581926590~9557935914"
    //const val DAILY_STATS = 0
    //const val YESTERDAY_STATS = 1
    //const val WEEKLY_STATS = 2
    const val MONTHLY_STATS = 3
    //const val NETWORK_MODE = 1
    var usageStatsManager: UsageStatsManager? = null

    fun initAppHelper(context: Context) {
        if (usageStatsManager == null)
            usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    fun getUsageStatsManager1(): UsageStatsManager? {
        return com.appnamenull.mlscheduler.Utils.StatsCalc.usageStatsManager
    }

    fun getTime(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return (hours % 24).toString() + ":" + minutes % 60 + ":" + seconds % 60
    }

    fun getHours(millis: Long): Float {
        val seconds = millis / 1000.toFloat()
        val minutes = seconds / 60
        return minutes / 60
    }

    fun getMinutes(millis: Long): Float {
        val seconds = millis / 1000.toFloat()
        return seconds / 60
    }
}