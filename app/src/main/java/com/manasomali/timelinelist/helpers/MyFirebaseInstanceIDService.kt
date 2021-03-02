package com.manasomali.timelinelist.helpers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.manasomali.timelinelist.R
import com.manasomali.timelinelist.activities.PesquisaActivity

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseInstanceIDService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("RemoteMessage", "From: " + remoteMessage.from);

        if (remoteMessage.notification != null) {
            Log.d("RemoteMessage", "Message Notification Body: " + (remoteMessage.notification?.body))
            notification(remoteMessage.notification?.title.toString(),remoteMessage.notification?.body.toString())
        }
    }
    fun notification(title: String, body: String) {
        val intent = Intent(this, PesquisaActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity( this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId =
            getString(R.string.default_notification_channel_id)
        val defaultSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION
            )
        val notificationBuilder = NotificationCompat.Builder(this,
            channelId)
            .setSmallIcon(R.drawable.icone)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 , notificationBuilder.build())
    }
}