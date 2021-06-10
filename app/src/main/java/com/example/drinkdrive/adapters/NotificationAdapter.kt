package com.example.drinkdrive.adapters

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.drinkdrive.R
import com.example.drinkdrive.activities.MainActivity

class NotificationAdapter : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val builder = NotificationCompat.Builder(context!!, "notification")
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("You can drive now")
            .setContentText("Concentration of alcohol in your body has dropped to 0‰")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()

        builder.contentIntent = PendingIntent.getActivity(context, 0,
            Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(1, builder)
    }
}