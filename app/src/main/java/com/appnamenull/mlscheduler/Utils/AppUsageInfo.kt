package com.appnamenull.mlscheduler.Utils
import android.graphics.drawable.Drawable

internal class AppUsageInfo(var packageName: String) {
    var appIcon // You may add get this usage data also, if you wish.
            : Drawable? = null
    var appName: String? = null
    var timeInForeground: Long = 0
    var launchCount = 0
}
