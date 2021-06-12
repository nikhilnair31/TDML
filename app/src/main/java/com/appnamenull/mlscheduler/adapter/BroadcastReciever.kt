package com.appnamenull.mlscheduler.adapter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import com.appnamenull.mlscheduler.R
import com.appnamenull.mlscheduler.TasksActivity


open class BroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        println("BROADCASTRECIVER onReceive start")
        val CHANNEL_ID = "3169"
        val notificationID = System.currentTimeMillis().toInt()//intent.getIntExtra("ID", 0)

        try {
            val vibrator : Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(250)
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mp = MediaPlayer.create(context, notification)
            mp.start()
        }
        catch (e: Exception) {
            val mp = MediaPlayer.create(context, R.raw.notif)
            mp.start()
            e.printStackTrace()
        }

        val intentToFire = Intent(context, TasksActivity::class.java)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intentToFire, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT).apply { description = context.getString(R.string.channel_description) }
            notificationManager.createNotificationChannel(notificationChannel)
            val builder = Notification.Builder(context, CHANNEL_ID )
                .setSmallIcon(R.drawable.ic_check_circle_white_24dp)
                .setContentTitle(intent.getStringExtra("Task"))
                .setContentText(intent.getStringExtra("Pred"))
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
            notificationManager.notify(notificationID, builder.build())
        }
        else{
            val builder = Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_check_circle_white_24dp)
                .setContentTitle(intent.getStringExtra("Task"))
                .setContentText(intent.getStringExtra("Deets"))
                .setContentIntent(pendingIntent)
            notificationManager.notify(notificationID, builder.build())
        }
    }

}