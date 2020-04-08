package com.appnamenull.mlscheduler.Utils

import android.app.usage.UsageStatsManager
import android.content.Context

object StatsCalc {
    //const val TAG = "TIME_TRACKER"
    //const val APP_ID = "ca-app-pub-3912594581926590~9557935914"
    //const val DAILY_STATS = 0
    //const val YESTERDAY_STATS = 1
    //const val WEEKLY_STATS = 2
    //const val MONTHLY_STATS = 3
    //const val NETWORK_MODE = 1
    private lateinit var usageStatsManager: UsageStatsManager

    fun initAppHelper(context: Context) { usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager }

    fun getUsageStatsManager1(): UsageStatsManager { return com.appnamenull.mlscheduler.Utils.StatsCalc.usageStatsManager }
}