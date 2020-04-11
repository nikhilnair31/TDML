package com.appnamenull.mlscheduler.Utils

import android.app.usage.UsageStats
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlin.math.floor

object PackageandTimeList {


    fun packagetoName(context: Context, apppackageList : MutableList<String>): MutableList<String>{
        val appnameList = mutableListOf<String>()
        for (pack in apppackageList) {
            val packageManager: PackageManager = context.packageManager
            val appName: String = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    pack,
                    PackageManager.GET_META_DATA
                )
            ).toString()
            appnameList.add(appName)
        }
        return appnameList
    }

    fun getPackageList(context: Context) : MutableList<String>{
        val pm : PackageManager = context.packageManager
        val appsList = mutableListOf<String>()
        val apps: List<ApplicationInfo> = pm.getInstalledApplications(0)
        for (app in apps) {
            if (app.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) > 0) {
                //appsList.add(app.packageName)
            }
            else {
                appsList.add(app.packageName)
            }
        }
        return appsList
    }

    fun getTimeList(apppackageList : MutableList<String>, statMap : Map<String, UsageStats>): MutableList<String>{
        val listofAppFGTime = mutableListOf<String>()
        for (element in apppackageList){
            val appPkg: String = element
            if (statMap.containsKey(appPkg)) {
                val seconds: Float = statMap[appPkg]!!.totalTimeInForeground / 1000.toFloat()
                val minutes = seconds / 60
                val hours = minutes / 60
                val total = hours
                val hr = Time.hourRound(100.0, total)
                listofAppFGTime.add("${floor(hr).toInt()}:${Time.minCalc(hr)}")
            }
        }
        return listofAppFGTime
    }

}