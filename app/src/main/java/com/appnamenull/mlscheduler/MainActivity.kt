package com.appnamenull.mlscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onUsageStatsPressed(view: View) {
        val intent = Intent(this, UsageStatsActivity::class.java)
        startActivity(intent)
    }

    fun onTasksPressed(view: View) {
        val intent = Intent(this, TasksActivity::class.java)
        startActivity(intent)
    }
}
