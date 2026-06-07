package com.kittys.premium.core.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kittys.premium.MainActivity
import com.kittys.premium.R

// ═══════════════════════════════════════════════════════
//   MIKO — Firebase Cloud Messaging Service
//   Push notifications: orders, promos, AI, escrow, seller
// ═══════════════════════════════════════════════════════

class MikoFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ORDERS  = "miko_orders"
        const val CHANNEL_PROMOS  = "miko_promos"
        const val CHANNEL_AI      = "miko_ai"
        const val CHANNEL_ESCROW  = "miko_escrow"
        const val CHANNEL_SELLER  = "miko_seller"
        const val CHANNEL_GENERAL = "miko_general"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: upload token to Supabase so the server can push to this device
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "MIKO"
        val body  = message.notification?.body  ?: message.data["message"] ?: ""
        val channel = message.data["channel"] ?: CHANNEL_GENERAL

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}