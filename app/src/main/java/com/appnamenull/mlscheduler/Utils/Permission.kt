package com.appnamenull.mlscheduler.Utils

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process

object Permission {

    private lateinit var usageStatsManager: UsageStatsManager

    fun initAppHelper(context: Context) {
        usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        println("initAppHelper")
    }

    fun getUsageStatsManager1(): UsageStatsManager { return usageStatsManager }

    fun checkUsageStatePermission(context: Context) : Boolean{
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow( AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }
}