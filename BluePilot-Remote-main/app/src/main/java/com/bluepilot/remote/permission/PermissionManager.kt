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
 * Minimal, privacy-first permission manager.
 * Only permissions required for Bluetooth HID are requested by default.
 * Only Bluetooth/notification permissions needed by production features are requested.
 */
@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun areAllPermissionsGranted(): Boolean = getMissingPermissions().isEmpty()

    fun getMissingPermissions(): List<String> = getRequiredRuntimePermissions().filterNot(::isPermissionGranted)

    fun getAllPermissions(): List<String> = getRequiredRuntimePermissions()

    fun getBluetoothPermissions(): List<String> = getRequiredRuntimePermissions().filter {
        it == Manifest.permission.BLUETOOTH_CONNECT ||
            it == Manifest.permission.BLUETOOTH_SCAN ||
            it == Manifest.permission.BLUETOOTH_ADVERTISE ||
            it == Manifest.permission.ACCESS_FINE_LOCATION ||
            it == Manifest.permission.BLUETOOTH ||
            it == Manifest.permission.BLUETOOTH_ADMIN
    }

    fun getNotificationPermission(): List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyList()
    }

    fun areBluetoothPermissionsGranted(): Boolean = getBluetoothPermissions().all(::isPermissionGranted)

    fun isNotificationPermissionGranted(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)

    private fun getRequiredRuntimePermissions(): List<String> {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions += Manifest.permission.BLUETOOTH_CONNECT
            permissions += Manifest.permission.BLUETOOTH_SCAN
            permissions += Manifest.permission.BLUETOOTH_ADVERTISE
        } else {
            permissions += Manifest.permission.ACCESS_FINE_LOCATION
            permissions += Manifest.permission.BLUETOOTH
            permissions += Manifest.permission.BLUETOOTH_ADMIN
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }
        return permissions.distinct()
    }

    private fun isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}
