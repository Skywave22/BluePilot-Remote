package com.bluepilot.remote.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bluepilot.remote.MainActivity
import com.bluepilot.remote.R
import com.bluepilot.remote.bluetooth.BluetoothHidManager
import com.bluepilot.remote.model.ConnectionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Foreground service for maintaining Bluetooth HID connection
 * Keeps the connection alive even when the app is in the background
 */
@AndroidEntryPoint
class HidService : Service() {

    @Inject
    lateinit var bluetoothHidManager: BluetoothHidManager

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var isServiceRunning = false

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "bluepilot_hid_channel"
        private const val CHANNEL_NAME = "BluePilot HID Connection"
        private const val ACTION_DISCONNECT = "com.bluepilot.remote.ACTION_DISCONNECT"
    }

    inner class LocalBinder : Binder() {
        fun getService(): HidService = this@HidService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeBluetooth()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_DISCONNECT -> {
                bluetoothHidManager.disconnect()
                stopForegroundService()
            }
            else -> {
                startForegroundService()
            }
        }
        return START_STICKY
    }

    /**
     * Initialize Bluetooth HID manager
     */
    private fun initializeBluetooth() {
        val initialized = bluetoothHidManager.initialize()
        if (initialized) {
            bluetoothHidManager.registerHidApp()
        }

        // Observe connection state and update notification
        bluetoothHidManager.connectionState.onEach { state ->
            updateNotification(state)
        }.launchIn(serviceScope)
    }

    /**
     * Start foreground service with notification
     */
    private fun startForegroundService() {
        if (isServiceRunning) return
        
        val notification = createNotification(
            ConnectionState.DISCONNECTED,
            "BluePilot Remote",
            "Disconnected"
        )
        
        startForeground(NOTIFICATION_ID, notification)
        isServiceRunning = true
    }

    /**
     * Stop foreground service
     */
    private fun stopForegroundService() {
        if (!isServiceRunning) return
        
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isServiceRunning = false
    }

    /**
     * Create notification channel for Android O+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Maintains Bluetooth HID connection"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Create notification based on connection state
     */
    private fun createNotification(
        state: ConnectionState,
        title: String,
        content: String
    ): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val disconnectIntent = Intent(this, HidService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getService(
            this,
            0,
            disconnectIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Disconnect",
                disconnectPendingIntent
            )
            .build()
    }

    /**
     * Update notification based on connection state
     */
    private fun updateNotification(state: com.bluepilot.remote.model.ConnectionStateData) {
        val (title, content) = when (state.state) {
            ConnectionState.CONNECTED -> {
                val deviceName = state.device?.name ?: "Device"
                "BluePilot Remote" to "Connected to $deviceName"
            }
            ConnectionState.CONNECTING -> {
                "BluePilot Remote" to "Connecting..."
            }
            ConnectionState.DISCONNECTED -> {
                "BluePilot Remote" to "Disconnected"
            }
            ConnectionState.PAIRING -> {
                "BluePilot Remote" to "Pairing..."
            }
            ConnectionState.BLUETOOTH_DISABLED -> {
                "BluePilot Remote" to "Bluetooth disabled"
            }
            ConnectionState.PERMISSION_MISSING -> {
                "BluePilot Remote" to "Permission required"
            }
            ConnectionState.HID_UNSUPPORTED -> {
                "BluePilot Remote" to "HID not supported"
            }
            ConnectionState.ERROR -> {
                "BluePilot Remote" to "Connection error"
            }
        }

        val notification = createNotification(state.state, title, content)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Get Bluetooth HID manager instance
     */
    fun obtainBluetoothHidManager(): BluetoothHidManager {
        return bluetoothHidManager
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothHidManager.cleanup()
    }
}
