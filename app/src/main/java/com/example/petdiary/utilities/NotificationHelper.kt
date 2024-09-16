package com.example.petdiary.utilities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.Timestamp
import android.content.BroadcastReceiver
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.petdiary.R
import com.example.petdiary.navigation.BottomBarScreen

class NotificationHelper(private val context: Context) {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleReminderNotification(reminderId: String, title: String, description: String, reminderTime: Timestamp) {
        Log.d("NotificationHelper", "Setting reminder for ID: $reminderId at ${reminderTime.toDate()}")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("reminderId", reminderId)
            putExtra("title", title)
            putExtra("description", description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, reminderId.hashCode(), notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, reminderTime.toDate().time, pendingIntent
        )
    }

    fun cancelReminderNotification(reminderId: String) {
        Log.d("NotificationHelper", "Cancelling notification for ID: $reminderId")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("reminderId", reminderId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, reminderId.hashCode(), notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d("NotificationHelper", "Notification cancelled for ID: $reminderId")
    }

    fun showNotification(reminderId: String, title: String, description: String) {
        Log.d("NotificationHelper", "Creating notification for ID: $reminderId")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, BottomBarScreen.RemindersScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reminderId", reminderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, reminderId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Kreiraj NotificationChannel za Android 8.0 i novije
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_notifications", "Pet Diary", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "reminder_notifications")
            .setSmallIcon(R.drawable.logo3)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(reminderId.hashCode(), notification)

        Log.d("NotificationHelper", "Notification sent for ID: $reminderId")
    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "BroadcastReceiver triggered")
        val title = intent.getStringExtra("title") ?: "Reminder"
        val description = intent.getStringExtra("description") ?: "You have a new reminder!"
        val reminderId = intent.getStringExtra("reminderId") ?: return

        // Prikaz notifikacije putem NotificationHelper-a
        NotificationHelper(context).showNotification(reminderId, title, description)
    }
}
