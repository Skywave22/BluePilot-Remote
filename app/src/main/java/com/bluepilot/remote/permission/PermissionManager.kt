package com.bluepilot.remote.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages app permissions for Bluetooth, camera, microphone, and notifications
 */
@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Check if all required permissions are granted
     */
    fun areAllPermissionsGranted(): Boolean {
        return getMissingPermissions().isEmpty()
    }

    /**
     * Get list of missing permissions
     */
    fun getMissingPermissions(): List<String> {
        val missing = mutableListOf<String>()

        // Bluetooth permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
                missing.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (!isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN)) {
                missing.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            if (!isPermissionGranted(Manifest.permission.BLUETOOTH_ADVERTISE)) {
                missing.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            }
        } else {
            // For older Android versions, location permission is needed for Bluetooth scanning
            if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                missing.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        // Legacy Bluetooth permissions (still needed on some devices)
        if (!isPermissionGranted(Manifest.permission.BLUETOOTH)) {
            missing.add(Manifest.permission.BLUETOOTH)
        }
        if (!isPermissionGranted(Manifest.permission.BLUETOOTH_ADMIN)) {
            missing.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        // Camera permission
        if (!isPermissionGranted(Manifest.permission.CAMERA)) {
            missing.add(Manifest.permission.CAMERA)
        }

        // Microphone permission
        if (!isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            missing.add(Manifest.permission.RECORD_AUDIO)
        }

        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)) {
                missing.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        return missing
    }

    /**
     * Check if a specific permission is granted
     */
    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get all required permissions
     */
    fun getAllPermissions(): List<String> {
        val permissions = mutableListOf<String>()

        // Bluetooth permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        permissions.add(Manifest.permission.BLUETOOTH)
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.RECORD_AUDIO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        return permissions
    }

    /**
     * Get Bluetooth-specific permissions
     */
    fun getBluetoothPermissions(): List<String> {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        permissions.add(Manifest.permission.BLUETOOTH)
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN)

        return permissions
    }

    /**
     * Get camera permission
     */
    fun getCameraPermission(): List<String> {
        return listOf(Manifest.permission.CAMERA)
    }

    /**
     * Get microphone permission
     */
    fun getMicrophonePermission(): List<String> {
        return listOf(Manifest.permission.RECORD_AUDIO)
    }

    /**
     * Get notification permission (Android 13+)
     */
    fun getNotificationPermission(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }
    }

    /**
     * Check if Bluetooth permissions are granted
     */
    fun areBluetoothPermissionsGranted(): Boolean {
        return getBluetoothPermissions().all { isPermissionGranted(it) }
    }

    /**
     * Check if camera permission is granted
     */
    fun isCameraPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.CAMERA)
    }

    /**
     * Check if microphone permission is granted
     */
    fun isMicrophonePermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.RECORD_AUDIO)
    }

    /**
     * Check if notification permission is granted
     */
    fun isNotificationPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        return isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)
    }
}
