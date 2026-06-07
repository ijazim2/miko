package com.kittys.premium

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.kittys.premium.core.notification.MikoFirebaseMessagingService
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

// ════════════════════════════════════════════════════════════════
//   MIKO APP — Hilt application entry point
//   Place at: app/src/main/java/com/kittys/premium/MikoApp.kt
// ════════════════════════════════════════════════════════════════

@HiltAndroidApp
class MikoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Timber debug logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Create notification channels on app start
        createNotificationChannels()

        Timber.d("MIKO app initialised")
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = getSystemService(NotificationManager::class.java) ?: return

        val channels = listOf(

            NotificationChannel(
                MikoFirebaseMessagingService.CHANNEL_ORDERS,
                "Order Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Track your orders in real-time"
                enableLights(true)
                enableVibration(true)
            },

            NotificationChannel(
                MikoFirebaseMessagingService.CHANNEL_PROMOS,
                "Deals & Promotions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Flash sales and exclusive promo codes"
            },

            NotificationChannel(
                MikoFirebaseMessagingService.CHANNEL_AI,
                "MIKO AI Picks",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Personalised AI outfit recommendations"
            },

            NotificationChannel(
                MikoFirebaseMessagingService.CHANNEL_ESCROW,
                "Payment & Delivery",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Buyer protection, refunds, and escrow updates"
                enableLights(true)
                enableVibration(true)
            },

            NotificationChannel(
                MikoFirebaseMessagingService.CHANNEL_SELLER,
                "Seller Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "New orders, low stock alerts, payouts"
                enableLights(true)
                enableVibration(true)
            },

            NotificationChannel(
                MikoFirebaseMessagingService.CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "General app notifications"
            }
        )

        channels.forEach { manager.createNotificationChannel(it) }

        Timber.d("Notification channels created (${channels.size})")
    }
}