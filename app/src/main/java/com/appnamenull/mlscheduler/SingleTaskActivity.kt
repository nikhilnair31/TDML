package com.appnamenull.mlscheduler

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appnamenull.mlscheduler.Utils.*
import com.appnamenull.mlscheduler.adapter.BroadcastReceiver
import com.appnamenull.mlscheduler.model.Task
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import kotlinx.android.synthetic.main.activity_single_task.*
import java.text.SimpleDateFormat
import java.util.*

class SingleTaskActivity : AppCompatActivity() {

    private lateinit var mydb : SavingSQLTasks
    private lateinit var mydbu : SavingSQLUsage
    private val sdf = SimpleDateFormat("dd.MM.yyyy\t\thh:mm a", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        if (extras != null) {
            txtTaskName.setText(extras.getString("taskname"))
            txtTaskDetail.setText(extras.getString("taskdeets"))
            txtTaskDate.text = extras.getString("taskdate")
            txtTaskPred.text = extras.getString("taskpred")
        }
        if(txtTaskDate.text.toString() == "")
            chartsUsage(0)
        else
            chartsForecast()
    }

    fun goBack(view : View) = finish()

    fun deleteTask(view : View){
        val extras = intent.extras
        if (extras != null) {
            val task = intent.getSerializableExtra("task") as? Task
            println("SINGLE deleteTask\ttask : $task\ttaskid : ${extras.getInt("taskid")}")
            mydb = SavingSQLTasks(applicationContext)
            if (task != null) {
                //mydb.deleteTask(task)
                //Cloud(this).deleteSpecificTasks(task.getId())
                mydb.updateTask(task, "YES COMP", "YES DEL")
                task.setCompStat("YES COMP")
                task.setDelStat("YES DEL")
                Cloud(this).updateSpecificTask(task, task.getId())
            }
            finish()
        }
    }
    fun doneEditingTask(view : View){
        mydb = SavingSQLTasks(applicationContext)
        val task = intent.getSerializableExtra("task") as? Task
        val taskName = txtTaskName.text.toString()
        val taskDetails = txtTaskDetail.text.toString()
        val taskDateTime = txtTaskDate.text.toString()
        if(taskName.isNotEmpty()) {
            if (task != null) {
                println("SINGLE doneEditingTask\ttaskid : ${task.getId()}\ttaskname : ${task.getTask()}")
                mydb.updateAll(task, taskName, taskDetails, taskDateTime)
                task.setTask(taskName)
                task.setDetails(taskDetails)
                task.setDateTime(taskDateTime)
                println("SINGLE doneEditingTask \ttaskid : ${task.getId()}\ttaskname : ${task.getTask()}")
                Cloud(this).updateTasks(task)
                println("SINGLE doneEditingTask pretoast")
                Toast.makeText(this, "Edited Task.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
    fun onDateSelect(view : View) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val taskTime = sdf.format(cal.time)
            txtTaskDate.setText(taskTime)

            val extras = intent.extras
            val intent = Intent(this, BroadcastReceiver::class.java)
            intent.putExtra("ID", taskId)
            intent.putExtra("Task", extras?.getString("taskname"))
            intent.putExtra("Pred", extras?.getString("taskpred"))
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val timeMs : Long = sdf.parse(taskTime).time
            am[AlarmManager.RTC_WAKEUP, timeMs] = pendingIntent
            println("SINGLE TimePickerDialog.OnTimeSetListener timeMs : $timeMs\n\n\n\n\n")
        }
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get( Calendar.DAY_OF_MONTH)).show()
    }

    private fun chartsForecast(){
        mydbu = SavingSQLUsage(applicationContext)
        val sqlAllUsage = mydbu.getUsageTillNow()
        var forecastList = doubleArrayOf()
        val daysize = 12
        val diff: Long = sdf.parse(txtTaskDate.text.toString()).time - System.currentTimeMillis()
        val hour = (diff / (1000 * 60 * 60)).toInt()
        if (hour > 0)
            forecastList = Forecasting.arima(sqlAllUsage, hour)
        var fsize = forecastList.size
        val num = sqlAllUsage.size
        val daynumsize = num - daysize
        println("SINGLE chartsForecast hour : $hour\tfsize : $fsize")
        if(fsize > daysize)
            fsize = daysize
        val dataPointsf = arrayOfNulls<DataPoint>(fsize+1)
        println("SINGLE chartsForecast fsize : $fsize\tnum : $num\tnum+fsize+1 : ${num+fsize+1}")
        for (j in num until (num+fsize+1)) {
            if(j - num == 0)
                dataPointsf[j - num] = DataPoint(j.toDouble(), 0.0)
            else {
                dataPointsf[j - num] = DataPoint(j.toDouble(), forecastList[j - num - 1])
                println("SINGLE chartsForecast j : $j\tj - num : ${j - num}\tforecastList[j - num - 1] : ${forecastList[j - num - 1]}\tdataPointsf[j - num] : ${dataPointsf[j - num]}")
            }

        }
        val seriesf = BarGraphSeries(dataPointsf)

        graphview.viewport.isXAxisBoundsManual = true
        graphview.viewport.setMaxX((num+fsize).toDouble())
        graphview.viewport.setMinX((daynumsize).toDouble())
        graphview.gridLabelRenderer.padding = 64
        graphview.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
        graphview.gridLabelRenderer.isVerticalLabelsVisible = false
        graphview.gridLabelRenderer.isHorizontalLabelsVisible = false
        graphview.gridLabelRenderer.reloadStyles()
        graphview.addSeries(seriesf)
        graphview.invalidate()

        val paintf = Paint()
        paintf.style = Paint.Style.FILL
        val linearGradientf = LinearGradient( 0f, 0f, 0f, 625f, getColor(R.color.colorAccentAnother), getColor(R.color.colorTransparent), Shader.TileMode.CLAMP )
        paintf.shader = linearGradientf
        seriesf.customPaint = paintf
        //seriesf.isDrawValuesOnTop = true
        seriesf.valuesOnTopColor = getColor(R.color.colorTertiaryText)
        seriesf.valuesOnTopSize = 15f
        seriesf.isAnimated = true

        chartsUsage(fsize)
    }

    private fun chartsUsage(fsize : Int){
        println("SINGLE CHART ${graphview.height.toFloat()}")

        mydbu = SavingSQLUsage(applicationContext)
        val sqlAllUsage = mydbu.getUsageTillNow()
        val num = sqlAllUsage.size
        val daysize = 12
        val daynumsize = num - daysize
        var fsizex = fsize
        if(fsizex > daysize)
            fsizex = 24
        val dataPointsu = arrayOfNulls<DataPoint>(daysize)
        for (i in daynumsize until num)
            dataPointsu[i-daynumsize] = DataPoint(i.toDouble(), sqlAllUsage[i])
        val seriesu = BarGraphSeries(dataPointsu)

        graphview.viewport.isXAxisBoundsManual = true
        graphview.viewport.setMaxX((num+fsizex).toDouble())
        graphview.viewport.setMinX((daynumsize).toDouble())
        graphview.gridLabelRenderer.padding = 64
        graphview.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
        graphview.gridLabelRenderer.isVerticalLabelsVisible = false
        graphview.gridLabelRenderer.isHorizontalLabelsVisible = false
        graphview.gridLabelRenderer.reloadStyles()
        graphview.addSeries(seriesu)
        graphview.invalidate()

        val paintu = Paint()
        paintu.style = Paint.Style.FILL
        val linearGradientu = LinearGradient( 0f, 0f, 0f, 625f, getColor(R.color.colorAccent), getColor(R.color.colorTransparent), Shader.TileMode.CLAMP )
        paintu.shader = linearGradientu
        seriesu.customPaint = paintu
        //seriesu.isDrawValuesOnTop = true
        seriesu.valuesOnTopColor = getColor(R.color.colorTertiaryText)
        seriesu.valuesOnTopSize = 15f
        seriesu.isAnimated = true
    }

}
