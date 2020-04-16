package com.appnamenull.mlscheduler

import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.appnamenull.mlscheduler.Utils.Cloud
import com.appnamenull.mlscheduler.Utils.Saving
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_usage_stats.*
import kotlinx.android.synthetic.main.new_task.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


//Use this class to potentialy get app Icon, Name, Launches etc.
internal class AppUsageInfo(var packageName: String) {
    //var appIcon  : Drawable? = null
    //var appName: String? = null
    //var launchCount = 0
    var timeInForeground: Float = 0F
}

class UsageStatsActivity : AppCompatActivity() {

    //Other vars
    private var starttime : Long = 0
    private var endtime : Long = 0
    lateinit var mUsageStatsManager : UsageStatsManager
    lateinit var networkStatsManager : NetworkStatsManager
    private lateinit var cal : Calendar
    private val dateformatter: DateFormat = SimpleDateFormat("dd-MM-yy", Locale.US)
    private val hourlyTime = mutableListOf<Int>()
    private val hourlyFGTime = mutableListOf<Int>()
    private val hourlyTxBytes = mutableListOf<Int>()
    private val tasksList = mutableListOf<String>()
    private  val userUsage = listOf("28,22,34,0,0,0,0,0,7,20,22,21,22,21,32,22,28,13,24,18,15,31,30,28,22,3,4,0,0,0,0,0,7,20,22,21,22,21,32,22,28,13,24,18,15,31,30," +
                                    "28,22,3,4,0,0,0,0,0,7,20,22,21,22,21,32,22,28,13,24,18,15,31,30,28,22,3,4,0,0,0,0,0,7,20,22,21,22,21,32,22,28,13,24,18,15,31,30," +
                                    "28,22,3,4,0,0,0,0,0,7,20,22,21,22,21,32,22,28,13,24,18,15,31,30,28,22,3,4,0,0,0,0,0,7,20,22,21,22,21,32,22,28,13,24,18,15,31,30," +
                                    "28,22,3,4,0,0,0,0,0,7,20,22,21,22,21,32,22,28,13,24,18,15,31,30,28,22,3,4,0,0,0,0,0,7,20,22,21,22,21,32,22,28,13,24,18,15,31,30," +
                                    "28,22,3,4,0,0,0,0,0,7,20,22,21,22,21,32,22,28,13,24,18,15,31,30")
    private val userUsageMap = userUsage.associateWith { it }

    //Shared Prefs
    private val mPreferences by lazy { getSharedPreferences("firstTimeCheck", Context.MODE_PRIVATE) }
    private val hourlyTimePref by lazy { getSharedPreferences("hourlyTimePref", Context.MODE_PRIVATE) }
    private val hourlyFGTimePref by lazy { getSharedPreferences("hourlyFGTimePref", Context.MODE_PRIVATE) }
    private val hourlyTxBytesPref by lazy { getSharedPreferences("hourlyTxBytesPref", Context.MODE_PRIVATE) }
    private val tasksandtimePref by lazy { getSharedPreferences("tasksandtimePref", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_stats)

        //Basic var init
        mUsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        networkStatsManager = getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        println("onCreate userUsageMap : $userUsageMap")
        if(checkUsageStatePermission())
            initRun()
        else
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart")
        if(checkUsageStatePermission())
            initRun()
        else
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    //To upload data to Firebase, add new task and clear all lists and shared prefs.
    fun onUploadPressed(view: View) = Cloud.pushString(txtDate.text as String, usagestatsText.text as String)
    fun onAddPressed(view: View) = addTask()
    fun onClearPressed(view: View) = clearAll()
    fun onTaskClickPressed(view: View) = Snackbar.make(view, "Clicked this task.", Snackbar.LENGTH_SHORT)

    //Obvious
    private fun checkUsageStatePermission() : Boolean{
        val appOpsManager = this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow( AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), this.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    //Checks if app's running for first time, thus either gets total usage till 9 days ago or gets usage in time difference
    private fun initRun() {
        println("firstTime : ${mPreferences.getBoolean("firstTimeCheck", true)}")
        val arrayAdapter : ArrayAdapter<String> = ArrayAdapter(this, R.layout.custom_textview, tasksList)
        listView.adapter = arrayAdapter
        if (mPreferences.getBoolean("firstTimeCheck", true))
            setupInit()
        else {
            Toast.makeText(this, "getAdditionalHours", Toast.LENGTH_SHORT).show()
            Saving.loadLists(hourlyTimePref, hourlyTime, "HourlyTime", hourlyFGTimePref, hourlyFGTime, "HourlyFGTime",
                hourlyTxBytesPref, hourlyTxBytes,  "HourlyTxBytes", tasksandtimePref, tasksList, "tasksandtimePref")
            getAdditionalHours()
            Saving.clearLists(hourlyTimePref, "hourlyTimePref", hourlyFGTimePref, "hourlyFGTimePref",
                hourlyTxBytesPref, "HourlyTxBytes", tasksandtimePref, "tasksandtimePref")
            Saving.saveLists(hourlyTimePref, hourlyTime, "HourlyTime", hourlyFGTimePref, hourlyFGTime, "HourlyFGTime",
                hourlyTxBytesPref, hourlyTxBytes, "HourlyTxBytes", tasksandtimePref, tasksList, "tasksandtimePref")
        }
    }

    //Clear lists and usage prefs
    private fun clearAll(){
        Toast.makeText(this, "Cleared All and Reloading Data", Toast.LENGTH_LONG).show()
        hourlyFGTime.clear()
        hourlyTime.clear()
        hourlyTxBytes.clear()
        println("Post Prefs HOURLYTIME : ${hourlyTime}\nHOURLYFGTIME : ${hourlyFGTime}\nHOURLYTXBYTES : ${hourlyTxBytes}\n")
        println("Post Clearing HOURLYTIME : ${hourlyTimePref.getString("HourlyTime", "")}\nHOURLYFGTIME : ${hourlyFGTimePref.getString("HourlyFGTime", "")}\nHOURLYTXBYTES : ${hourlyTxBytesPref.getString("HourlyTxBytes", "")}\n")
        Saving.clearLists(hourlyTimePref, "hourlyTimePref", hourlyFGTimePref, "hourlyFGTimePref",
            hourlyTxBytesPref, "HourlyTxBytes", tasksandtimePref, "tasksandtimePref")
        getTotalUsageOverTime()
        Saving.saveLists(hourlyTimePref, hourlyTime, "HourlyTime", hourlyFGTimePref, hourlyFGTime, "HourlyFGTime",
            hourlyTxBytesPref, hourlyTxBytes, "HourlyTxBytes", tasksandtimePref, tasksList, "tasksandtimePref")
    }

    //Create new task
    private fun addTask(){
        //Toast.makeText(this, "Added Task.", Toast.LENGTH_LONG).show()
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.new_task, null)
        val mBuilder = AlertDialog.Builder(this, R.style.DialogTheme).setView(mDialogView)
        val  mAlertDialog = mBuilder.show()
        mDialogView.btnDialogAddTask.setOnClickListener {
            mAlertDialog.dismiss()
            val taskname = mDialogView.taskname.text.toString()
            val taskdate = mDialogView.taskdate.text.toString()
            val taskStr = "$taskname-$taskdate"
            tasksList.add((taskStr))
            val arrayAdapter : ArrayAdapter<String> = ArrayAdapter(this, R.layout.custom_textview, tasksList)
            listView.adapter = arrayAdapter
            println("tasksList : $tasksList")
            Saving.saveLists(hourlyTimePref, hourlyTime, "HourlyTime", hourlyFGTimePref, hourlyFGTime, "HourlyFGTime",
                hourlyTxBytesPref, hourlyTxBytes, "HourlyTxBytes", tasksandtimePref, tasksList, "tasksandtimePref")
        }
        mDialogView.btnDialogCancelTask.setOnClickListener {mAlertDialog.dismiss()}
    }

    //To get usage in time from last use of app to put into list
    private fun getAdditionalHours(){
        cal = setCalInit()
        starttime = cal.timeInMillis
        val num : Int = ((System.currentTimeMillis()-starttime)/(3600000)).toInt()- 1
        val diffInHours = hourlyTime.last() - num
        println("starttime : $starttime\nSystem.currentTimeMillis() : ${System.currentTimeMillis()}\nnum : $num\ndiffInHours : $diffInHours")
        if(diffInHours == 0)
            Toast.makeText(this, "Its now bb", Toast.LENGTH_LONG).show()
        else if(diffInHours > 0){
            for(i in 0 until diffInHours) {
                Toast.makeText(this, "some time has passed", Toast.LENGTH_LONG).show()
                starttime = System.currentTimeMillis()-(60*60*1000 * (diffInHours-i))
                endtime = starttime + (1000 * 60 * 60.toLong())
                hourlyTime.add(hourlyTime.last() + 1)
                hourlyTxBytes.add( hourlyTxBytesWM() )
                setUsageInfo()
            }
        }
        println("Additional HOURLYTIME : ${hourlyTime}\nHOURLYFGTIME : ${hourlyFGTime}\nHOURLYTXBYTES : ${hourlyTxBytes}\n")
    }

    private fun getTotalUsageOverTime() {
        var str = ""
        hourlyFGTime.clear()
        hourlyTime.clear()
        hourlyTxBytes.clear()

        cal = setCalInit()
        txtDate.text = "From : "+ dateformatter.format(cal.time)
        starttime = cal.timeInMillis
        val num : Int = ((System.currentTimeMillis()-starttime)/(3600000)).toInt()
        endtime = starttime
        for(i in 1 until num){
            starttime = endtime
            endtime = starttime + (1000 * 60 * 60.toLong())
            hourlyTime.add(i)
            hourlyTxBytes.add(hourlyTxBytesWM())
            setUsageInfo()
            //str += hourlyTime[i-1].toString()+" : "+ hourlyFGTime[i]+" min "+(hourlyTxBytes[i])+" MB\n"
        }
        for(i in 0 until hourlyTime.size) {
            str += hourlyTime[i].toString()+" : "+ hourlyFGTime[i]+" min "+(hourlyTxBytes[i])+" MB\n"
        }
        println("Total HOURLYTIME : ${hourlyTime}\nHOURLYFGTIME : ${hourlyFGTime}\nHOURLYTXBYTES : ${hourlyTxBytes}\n")
        usagestatsText.text = str
    }

    private fun setUsageInfo(){
        var currentEvent: UsageEvents.Event
        val allEvents: MutableList<UsageEvents.Event> = ArrayList()
        val map: HashMap<String, AppUsageInfo> = HashMap()

        val usageEvents = mUsageStatsManager.queryEvents(starttime, endtime)
        while (usageEvents.hasNextEvent()) {
            currentEvent = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)
            if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED ) {
                allEvents.add(currentEvent)
                val key = currentEvent.packageName
                if (map[key] == null)
                    map[key] = AppUsageInfo(key)
            }
        }
        for (i in 0 until allEvents.size - 1) {
            val event0 = allEvents[i]
            val event1 = allEvents[i + 1]
            if (event0.eventType == 1 && event1.eventType == 2 && event0.className == event1.className ) {
                val diff = event1.timeStamp - event0.timeStamp
                map[event0.packageName]?.timeInForeground = map[event0.packageName]?.timeInForeground?.plus(diff)!!
            }
        }
        val smallInfoList = ArrayList(map.values)
        var hourUse = 0
        for (appUsageInfo in smallInfoList) {
            hourUse += (appUsageInfo.timeInForeground/60000).toInt()
        }
        hourlyFGTime.add(hourUse)
    }

    private fun setCalInit(): Calendar{
        val calendar: Calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = -216
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar
    }

    private fun setupInit(){
        Toast.makeText(this, "setupInit !firstTime", Toast.LENGTH_SHORT).show()
        mPreferences.edit().putBoolean("firstTimeCheck", false).apply()
        println("setupInit userUsageMapE : $userUsageMap")
        getTotalUsageOverTime()
        Saving.saveLists(hourlyTimePref, hourlyTime, "HourlyTime", hourlyFGTimePref, hourlyFGTime, "HourlyFGTime",
            hourlyTxBytesPref, hourlyTxBytes, "HourlyTxBytes", tasksandtimePref, tasksList, "tasksandtimePref")
    }

    private fun hourlyTxBytesWM(): Int{
        val bucketw: NetworkStats.Bucket = networkStatsManager.querySummaryForDevice( ConnectivityManager.TYPE_WIFI, "", starttime, endtime )
        val bucketm: NetworkStats.Bucket = networkStatsManager.querySummaryForDevice( ConnectivityManager.TYPE_MOBILE, "", starttime, endtime )
        return (bucketw.txBytes+bucketm.txBytes/1000000).toInt()
    }

}
