package com.bluepilot.remote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepilot.remote.permission.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main ViewModel for managing app-wide state
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _arePermissionsGranted = MutableStateFlow(permissionManager.areAllPermissionsGranted())
    val arePermissionsGranted: StateFlow<Boolean> = _arePermissionsGranted.asStateFlow()

    init {
        checkPermissions()
    }

    /**
     * Check if all permissions are granted
     */
    fun checkPermissions() {
        _arePermissionsGranted.value = permissionManager.areAllPermissionsGranted()
    }

    /**
     * Handle permission request results
     */
    fun onPermissionResult(permissions: Map<String, Boolean>) {
        viewModelScope.launch {
            checkPermissions()
        }
    }

    /**
     * Get list of missing permissions
     */
    fun getMissingPermissions(): List<String> {
        return permissionManager.getMissingPermissions()
    }

    /**
     * Get all required permissions
     */
    fun getAllPermissions(): List<String> {
        return permissionManager.getAllPermissions()
    }
}
