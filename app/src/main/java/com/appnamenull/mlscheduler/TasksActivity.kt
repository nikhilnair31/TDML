package com.appnamenull.mlscheduler

import android.app.*
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appnamenull.mlscheduler.Utils.Cloud
import com.appnamenull.mlscheduler.Utils.SavingSQLTasks
import com.appnamenull.mlscheduler.Utils.SavingSQLUsage
import com.appnamenull.mlscheduler.adapter.BroadcastReceiver
import com.appnamenull.mlscheduler.adapter.RecycleAdapter
import com.appnamenull.mlscheduler.adapter.RecycleAdapterC
import com.appnamenull.mlscheduler.model.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_tasks.*
import java.text.SimpleDateFormat
import java.util.*

internal class AppUsageInfo(var packageName: String) {
    //var appIcon  : Drawable? = null
    //var appName: String? = null
    //var launchCount = 0
    var timeInForeground: Float = 0F
}

class TasksActivity : AppCompatActivity() {

    private lateinit var mydb : SavingSQLTasks
    private lateinit var sqltaskList: ArrayList<Task>
    private  lateinit var rAdapter : RecycleAdapter
    private  lateinit var cAdapter : RecycleAdapterC

    //Other vars
    var opened = false
    private var starttime : Long = 0
    private var endtime : Long = 0
    private lateinit var mUsageStatsManager : UsageStatsManager
    private lateinit var networkStatsManager : NetworkStatsManager
    private lateinit var cal : Calendar
    private lateinit var mydbu : SavingSQLUsage

    //Shared Prefs
    private val lastCheckedTimePref by lazy { getSharedPreferences("lastcheckedtime", Context.MODE_PRIVATE) }
    private val mPreferences by lazy { getSharedPreferences("firstTime", Context.MODE_PRIVATE) }
    private val themePref by lazy { getSharedPreferences("theme", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_tasks)

        mydb = SavingSQLTasks(this)
        init()
        if (mPreferences.getBoolean("firstTime", true)){
            println("TASKSACTIVITY onCreate firstTime : true")
            mPreferences!!.edit().putBoolean("firstTime", false).apply()
            mydb.deleteAll()
            mCheckInforInServer()
        }
        else{
            println("TASKSACTIVITY onCreate firstTime : false")
            refreshDB()
            initStuff()
        }
    }

    private fun mCheckInforInServer() {
        Cloud(this).mReadDataOnce(object : Cloud.OnGetDataListener {
            override fun onStart() {
                println("TASKSACTIVITY onStart")
            }
            override fun onSuccess(taskList: List<Task>?) {
                println("TASKSACTIVITY onSuccess $taskList")
                sqltaskList = taskList as ArrayList<Task>
                for(i in sqltaskList.indices) {
                    val task = sqltaskList[i]
                    mydb.insertAll( task.getTask(), task.getDetails(), task.getDateTime(), task.getCompStat(), task.getDelStat())
                    println("TASKSACTIVITY onSuccess task : ${task}\ttask : ${task.getTask()}\ttaskdeets : ${task.getDetails()}\ttaskdatetime : ${task.getDateTime()}\t")
                }
                Cloud(this@TasksActivity).deleteAllTasks()
                Cloud(this@TasksActivity).pushAllTasks(mydb.getAllTask()!!)
                refreshDB()
                initStuff()
            }
            override fun onFailed(databaseError: DatabaseError?) {
                println("TASKSACTIVITY databaseError $databaseError")
                sqltaskList = mydb.getAllTask()!!
                refreshDB()
                initStuff()
            }
        })
    }

    private  fun initStuff(){
        recyclerView.layoutManager = LinearLayoutManager(this@TasksActivity)
        val dividerItemDecoration = DividerItemDecoration( recyclerView.context, (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerViewC.layoutManager = LinearLayoutManager(this@TasksActivity)
        val dividerItemDecorationC = DividerItemDecoration( recyclerViewC.context, (recyclerViewC.layoutManager as LinearLayoutManager).orientation)
        recyclerViewC.addItemDecoration(dividerItemDecorationC)
        val itemTouchHelperC = ItemTouchHelper(itemTouchHelperCallbackC)
        itemTouchHelperC.attachToRecyclerView(recyclerViewC)
    }

    private fun refreshDB(){
        rAdapter = RecycleAdapter( mydb.getAllIncompTask()!! )
        recyclerView.adapter = rAdapter

        cAdapter = RecycleAdapterC(mydb.getAllCompTask()!!)
        recyclerViewC.adapter = cAdapter
    }

    override fun onPause() {
        super.onPause()
        println("TASKSACTIVITY ONPAUSE")
        //ReOrdering
    }

    override fun onResume() {
        super.onResume()
        println("TASKSACTIVITY ONRESUME")
        refreshDB()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //Delete tasks with delstat as yes from Firebase
        mydb = SavingSQLTasks(this)
        mydb.deleteTaskbyDelStat()
        ActivityCompat.finishAffinity(this)
    }

    fun toSeeHideCompTasks(view: View) {
        if (!opened) {
//            val collapseAnim = TranslateAnimation(0.0f, 0.0f, 0f, (-recyclerViewC.height).toFloat())
//            collapseAnim.setAnimationListener(object : Animation.AnimationListener {
//                override fun onAnimationRepeat(p0: Animation?){}
//                override fun onAnimationEnd(animation: Animation?) = recyclerViewC.visibility = View.VISIBLE
//                override fun onAnimationStart(p0: Animation?){}
//            })
//            collapseAnim.duration = 1000
//            collapseAnim.interpolator = AccelerateInterpolator(0.5f)
//            rlCompTasks.startAnimation(collapseAnim)

            recyclerViewC.visibility = View.VISIBLE
            val animate = TranslateAnimation( 0f, 0f, recyclerViewC.height.toFloat(), 0f )
            animate.duration = 100
            animate.fillAfter = true
            recyclerViewC.startAnimation(animate)
        }
        else {
//            val collapseAnim = TranslateAnimation(0.0f, 0.0f, 0.0f, (recyclerViewC.height).toFloat())
//            collapseAnim.setAnimationListener(object : Animation.AnimationListener {
//                override fun onAnimationRepeat(p0: Animation?){}
//                override fun onAnimationEnd(animation: Animation?) = recyclerViewC.visibility = View.GONE
//                override fun onAnimationStart(p0: Animation?){}
//            })
//            collapseAnim.duration = 500
//            collapseAnim.interpolator = AccelerateInterpolator(0.5f)
//            rlCompTasks.startAnimation(collapseAnim)

            val animate = TranslateAnimation( 0f, 0f, recyclerViewC.height.toFloat(), 0f  )
            animate.duration = 200
            animate.fillAfter = true
            recyclerViewC.startAnimation(animate)
            recyclerViewC.visibility = View.GONE
            recyclerViewC.isClickable = false
        }
        ivExpand.scaleY *= -1
        opened = !opened
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun isLongPressDragEnabled(): Boolean = true
        override fun isItemViewSwipeEnabled(): Boolean = true
        override fun getMovementFlags( recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder ): Int =
            ItemTouchHelper.Callback.makeMovementFlags( (ItemTouchHelper.UP or ItemTouchHelper.DOWN), (ItemTouchHelper.END) )
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
            sqltaskList = mydb.getAllIncompTask()!!
            val startpos = viewHolder.adapterPosition
            val endpos = target.adapterPosition
            Collections.swap(sqltaskList, startpos, endpos)
            recyclerView.adapter?.notifyItemMoved(startpos, endpos)
            return false
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            //Fix crash on trying to restore cause suitable parent not found
//            val snackbar = Snackbar.make(viewHolder.itemView, "Completed.", Snackbar.LENGTH_LONG).setAction("Restore?") {
//                val snackbar1 = Snackbar.make(viewHolder.itemView,"Restored.",Snackbar.LENGTH_LONG)
//                snackbar1.show()
//            }
//            snackbar.show()
            sqltaskList = mydb.getAllIncompTask()!!
            //println("TASKSACTIVITY SWIPED : ${sqltaskList[pos].getTask()}")
            mydb.updateTask(sqltaskList[pos], "YES COMP", "NOT DEL")
            sqltaskList[pos].setCompStat("YES COMP")
            sqltaskList[pos].setDelStat("NOT DEL")
            Cloud(this@TasksActivity).updateSpecificTask(sqltaskList[pos], sqltaskList[pos].getId())
            refreshDB()
        }
    }

    private val itemTouchHelperCallbackC = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun isItemViewSwipeEnabled(): Boolean = true
        override fun onMove(recyclerViewC: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
            return false
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            sqltaskList = mydb.getAllCompTask()!!
            println("TASKSACTIVITY C SWIPED : ${sqltaskList[pos].getTask()}")
            mydb.updateTask(sqltaskList[pos], "NOT COMP", "NOT DEL")
            sqltaskList[pos].setCompStat("NOT COMP")
            sqltaskList[pos].setDelStat("NOT DEL")
            Cloud(this@TasksActivity).updateSpecificTask(sqltaskList[pos], sqltaskList[pos].getId())
            refreshDB()
        }
    }

    fun addNewTask(view: View?) {
        val dialog = Dialog(this, R.style.DialogTheme)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(true)
        dialog .setContentView(R.layout.new_task)
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setLayout( WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT )
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        val taskname: EditText = dialog.findViewById(R.id.taskname)
        val taskdetails: EditText = dialog.findViewById(R.id.txtTaskDetails)
        val taskdatetime: EditText = dialog.findViewById(R.id.txtTaskDate)
        val btnDialogDateTime: ImageButton = dialog.findViewById(R.id.btnDialogDateTime)
        val btnDialogDetails: ImageButton = dialog.findViewById(R.id.btnDialogDetails)
        val btnDialogAddTask: ImageButton = dialog.findViewById(R.id.btnDialogAddTask)
        taskname.requestFocus()
        var taskName: String
        var taskDetails: String
        var taskDateTime : String
        var taskTime : String

        dialog.setOnCancelListener {
            if (view != null) {
                val immm = (this as AppCompatActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                immm.hideSoftInputFromWindow( (this as AppCompatActivity).currentFocus?.windowToken, 0 )
            }
        }

        btnDialogAddTask.setOnClickListener {
            dialog.dismiss()
            var idOfInsertedTask = 0
            taskName = taskname.text.toString()
            taskDetails = taskdetails.text.toString()
            taskDateTime = taskdatetime.text.toString()
            if(taskName.isNotEmpty()) {
                idOfInsertedTask = mydb.insertAll(taskName, taskDetails, taskDateTime, "NOT COMP", "NOT DEL")
                refreshDB()
                Toast.makeText(this, "Added Task.", Toast.LENGTH_LONG).show()
            }
            Cloud(this).pushTasks(mydb.getAllTask()!!, idOfInsertedTask)
        }

        btnDialogDetails.setOnClickListener {
            if(taskdetails.visibility == View.GONE) taskdetails.visibility = View.VISIBLE
            else if(taskdetails.visibility == View.VISIBLE) taskdetails.visibility = View.GONE
            taskdetails.requestFocus()
        }

        btnDialogDateTime.setOnClickListener {
            Toast.makeText(this, "Pick Date/Time.", Toast.LENGTH_LONG).show()
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                taskTime = SimpleDateFormat("dd.MM.yyyy\t\thh:mm a", Locale.ENGLISH).format(cal.time)
                taskdatetime.setText(taskTime)

                val intent = Intent(this, BroadcastReceiver::class.java)
                intent.putExtra("ID", taskId.toString())
                intent.putExtra("Task", taskname.toString())
                intent.putExtra("Deets", taskdetails.toString())
                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val timeMs : Long = SimpleDateFormat("dd.MM.yyyy\t\thh:mm a", Locale.ENGLISH).parse(taskTime).time
                am[AlarmManager.RTC_WAKEUP, timeMs] = pendingIntent
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            if(taskdatetime.visibility == View.GONE) taskdatetime.visibility = View.VISIBLE
        }
        dialog .show()
    }

    fun clearAll(view : View){
        mydb.deleteAll()
        refreshDB()
        Cloud(this).deleteAllTasks()
        Toast.makeText(this, "Cleared all tasks", Toast.LENGTH_LONG).show()
    }

    private fun init() {
        println("init")
        mydbu = SavingSQLUsage(this)
        mUsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        networkStatsManager = getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        if (mPreferences.getBoolean("firstTime", true)){
            println("USAGESTATS firstTime true")
            getTotalUsageOverTime()
        }
        else {
            println("USAGESTATS firstTime false")
            getAdditionalHours()
        }
    }

    //To get usage in time from last use of app to put into list
    private fun getAdditionalHours(){
        starttime = lastCheckedTimePref.getLong("lastcheckedtime", 0)
        println("LASTCHECKEDTIME GET : $starttime")
        val num : Int = ((System.currentTimeMillis()-starttime)/(3600000)).toInt()//- 1
        println("starttime : $starttime\nSystem.currentTimeMillis() : ${System.currentTimeMillis()}\nnum : $num\nmydb.getLastId() : ${mydbu.getLastId()}")
        if(num <= 0)
            //Toast.makeText(this, "Its now bb", Toast.LENGTH_LONG).show()
        else if(num > 0){
            lastCheckedTimePref.edit().putLong("lastcheckedtime", System.currentTimeMillis()).apply()
            for(i in 0 until num) {
                //Toast.makeText(this, "some time has passed", Toast.LENGTH_LONG).show()
                starttime = System.currentTimeMillis()-(60*60*1000 * (num-i))
                endtime = starttime + (1000 * 60 * 60.toLong())
                mydbu.insertAll(mydbu.getLastId(), setUsageInfo(), hourlyTxBytesWM())
            }
        }
    }

    private fun getTotalUsageOverTime() {
        mydbu.deleteAllUsage()
        cal = setCalInit()
        starttime = cal.timeInMillis
        lastCheckedTimePref.edit().putLong("lastcheckedtime", System.currentTimeMillis()).apply()
        println("LASTCHECKEDTIME SET : ${System.currentTimeMillis()}")
        val num : Int = ((System.currentTimeMillis()-starttime)/(3600000)).toInt()
        endtime = starttime
        for(i in 1 until num){
            starttime = endtime
            endtime = starttime + (1000 * 60 * 60.toLong())
            mydbu.insertAll(i, setUsageInfo(), hourlyTxBytesWM())
        }
    }

    private fun setUsageInfo(): Double{
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
        var hourUse = 0.0
        for (appUsageInfo in smallInfoList) {
            hourUse += (appUsageInfo.timeInForeground/60000).toDouble()
        }
        return hourUse
    }

    private fun setCalInit(): Calendar {
        val calendar: Calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = -216
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar
    }

    private fun hourlyTxBytesWM(): Int{
        val bucketw: NetworkStats.Bucket = networkStatsManager.querySummaryForDevice( ConnectivityManager.TYPE_WIFI, "", starttime, endtime )
        val bucketm: NetworkStats.Bucket = networkStatsManager.querySummaryForDevice( ConnectivityManager.TYPE_MOBILE, "", starttime, endtime )
        return (bucketw.txBytes+bucketm.txBytes/1000000).toInt()
    }

    fun applyTheme(view : View) {
        when (Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {} // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {} // Night mode is active, we're using dark theme
        }
    }

}
