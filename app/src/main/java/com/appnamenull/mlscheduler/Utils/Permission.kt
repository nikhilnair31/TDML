package com.appnamenull.mlscheduler.Utils

import android.Manifest
import android.annotation.TargetApi
import android.app.AppOpsManager
import android.app.AppOpsManager.OnOpChangedListener
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity

object Permission {

//    private lateinit var usageStatsManager: UsageStatsManager

//    fun initAppHelper(context: Context) {
//        usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        println("initAppHelper")
//    }

//    fun getUsageStatsManager1(): UsageStatsManager { return usageStatsManager }

    fun checkUsageStatePermission(context: Context) : Boolean{
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow( AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

//    fun hasPermissionToReadPhoneStats(context: Context): Boolean {
//        return ActivityCompat.checkSelfPermission( context, Manifest.permission.READ_PHONE_STATE ) !== PackageManager.PERMISSION_DENIED
//    }

//    private fun requestPhoneStateStats() {
//        ActivityCompat.requestPermissions( this, arrayOf(Manifest.permission.READ_PHONE_STATE), pl.rzagorski.networkstatsmanager.view.StatsActivity.READ_PHONE_STATE_REQUEST )
//    }

//    private fun hasPermissionToReadNetworkHistory(): Boolean {
//        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        val mode = appOps.checkOpNoThrow(
//            AppOpsManager.OPSTR_GET_USAGE_STATS,
//            Process.myUid(), getPackageName()
//        )
//        if (mode == AppOpsManager.MODE_ALLOWED) {
//            return true
//        }
//        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
//            getApplicationContext().getPackageName(),
//            object : OnOpChangedListener {
//                @TargetApi(Build.VERSION_CODES.M)
//                override fun onOpChanged(op: String, packageName: String) {
//                    val mode = appOps.checkOpNoThrow(
//                        AppOpsManager.OPSTR_GET_USAGE_STATS,
//                        Process.myUid(), getPackageName()
//                    )
//                    if (mode != AppOpsManager.MODE_ALLOWED) {
//                        return
//                    }
//                    appOps.stopWatchingMode(this)
//                    val intent = Intent(
//                        this@StatsActivity,
//                        pl.rzagorski.networkstatsmanager.view.StatsActivity::class.java
//                    )
//                    if (getIntent().getExtras() != null) {
//                        intent.putExtras(getIntent().getExtras())
//                    }
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    getApplicationContext().startActivity(intent)
//                }
//            })
//        requestReadNetworkHistoryAccess()
//        return false
//    }
//
//    private fun requestReadNetworkHistoryAccess() {
//        //startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
//        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//        startActivity(intent)
//    }
}