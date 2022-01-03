package com.app.todo.posts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_LIGHT
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.app.MainActivity
import com.app.R
import java.util.concurrent.TimeUnit

class NotifWorker (
    context: Context,
    workerParams: WorkerParameters
    ) : Worker(context, workerParams) {
        override fun doWork(): ListenableWorker.Result {
            // perform long running operation
            val delay: Int = inputData.getInt("delay", 0)
            for (i in 1..delay) {
                TimeUnit.SECONDS.sleep(1)
                Log.d("Worker", "progress: $i / $delay")
            }
            createNotificationChannel()
            createNotification(delay, inputData.getFloat("light", 0f))
            return ListenableWorker.Result.success()
        }
    val CHANNEL_ID = "CHANNEL_ID"

    private fun createNotification(x: Int, lx: Float) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("The Message")
            .setContentText("Message requested $x seconds ago. The light sensor is reading: $lx")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My channel name"
            val descriptionText = "My channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    }