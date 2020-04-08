package com.appnamenull.mlscheduler.Utils

import android.app.AppOpsManager
import android.content.Context
import android.os.Process

object Permission {

    fun checkUsageStatePermission(context: Context) : Boolean{
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow( AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }
}