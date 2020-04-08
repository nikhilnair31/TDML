package com.appnamenull.mlscheduler.Utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object Packages {

    private val appnameList = mutableListOf<String>()

    private fun packagetoName(context: Context, packageName: String){
        val packageManager : PackageManager = context.packageManager;
        val appName : String = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
        appnameList.add(appName)
    }

    fun getAllPackages(context: Context) : MutableList<String>{
        val pm : PackageManager = context.packageManager
        val appsList = mutableListOf<String>()
        val apps: List<ApplicationInfo> = pm.getInstalledApplications(0)
        for (app in apps) {
            if (app.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) > 0) {
                //println("It is a system app : "+app.packageName)
            }
            else {
                appsList.add(app.packageName)
            }
        }
        return appsList
    }
}