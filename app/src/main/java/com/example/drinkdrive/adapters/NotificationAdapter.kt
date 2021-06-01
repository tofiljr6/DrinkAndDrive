package com.example.drinkdrive.adapters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.drinkdrive.R

class NotificationAdapter : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val builder = NotificationCompat.Builder(context!!, "notification")
            .setSmallIcon(R.drawable.button_icon)
            .setContentTitle("Możesz już prowadzić")
            .setContentText("Stężenie alkoholu w twoim organiźmie spadło do 0‰")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(1, builder.build())
    }
}