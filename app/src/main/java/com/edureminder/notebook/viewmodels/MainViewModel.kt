package com.edureminder.notebook.viewmodels

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val permissionDialogQueue = mutableStateListOf<String>()

    // Flow to expose Pro status
    private val _isPro = MutableStateFlow(true) // default false
    val isPro: StateFlow<Boolean> = _isPro

    // Function to update Pro status (e.g. from DataStore or purchase result)
    fun updateProStatus(isUserPro: Boolean) {
        _isPro.value = isUserPro
    }

    /**
     * Handles the result of a permission request.
     * If the permission is not granted, it adds the permission to the dialog queue.
     */
    fun onPermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted) {
            permissionDialogQueue.add(0, permission)
        }
    }

    /**
     * Check if the permission is granted and navigate accordingly.
     * If the permission is not granted, it will show the permission screen.
     * If the permission is already granted, it will navigate to the home screen.
     */
    fun checkAndRequestPermission(
        context: Context,
        permission: String,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, navigate to the home screen
            onPermissionGranted()
        } else {
            // Permission is not granted, show the permission screen
            onPermissionDenied()
        }
    }

    /**
     * Opens the auto-start settings for different manufacturers.
     * If no manufacturer-specific setting is found, opens general app settings.
     */
    fun enableAutoStartIntent(context: Context) {
        val intent = Intent()
        val manufacturer = Build.MANUFACTURER.lowercase()

        when {
            manufacturer.contains("xiaomi") -> {
                intent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            manufacturer.contains("oppo") -> {
                intent.setClassName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            }
            manufacturer.contains("vivo") -> {
                intent.setClassName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            }
            manufacturer.contains("huawei") -> {
                intent.setClassName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
            }
            manufacturer.contains("samsung") -> {
                intent.action = "com.samsung.android.sm.ACTION_BATTERY"
            }
            manufacturer.contains("oneplus") -> {
                intent.setClassName(
                    "com.oneplus.security",
                    "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
                )
            }
            manufacturer.contains("realme") -> {
                intent.setClassName(
                    "com.oppo.safe",
                    "com.oppo.safe.permission.startup.StartupAppListActivity"
                )
            }
            manufacturer.contains("asus") -> {
                intent.setClassName(
                    "com.asus.mobilemanager",
                    "com.asus.mobilemanager.powersaver.PowerSaverSettings"
                )
            }
            manufacturer.contains("nokia") -> {
                intent.setClassName(
                    "com.evenwell.powersaving.g3",
                    "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity"
                )
            }
            manufacturer.contains("lenovo") -> {
                intent.setClassName(
                    "com.lenovo.security",
                    "com.lenovo.security.powersaver.BgStartUpManagerActivity"
                )
            }
            manufacturer.contains("zte") -> {
                intent.setClassName(
                    "com.zte.heartyservice",
                    "com.zte.heartyservice.autostart.AutoStartManagementActivity"
                )
            }
            manufacturer.contains("meizu") -> {
                intent.setClassName(
                    "com.meizu.safe",
                    "com.meizu.safe.permission.SmartBGActivity"
                )
            }
            manufacturer.contains("gionee") -> {
                intent.setClassName(
                    "com.gionee.softmanager",
                    "com.gionee.softmanager.MainActivity"
                )
            }
            manufacturer.contains("sony") -> {
                intent.setClassName(
                    "com.sonymobile.cta",
                    "com.sonymobile.cta.ui.PermissionCheckerActivity"
                )
            }
            manufacturer.contains("infinix") -> {
                intent.setClassName(
                    "com.transsion.phoenix",
                    "com.transsion.phoenix.activity.BgStartUpManagerActivity"
                )
            }
            else -> {
                // Open general app settings if no manufacturer-specific setting is found
                openAppSettings(context)
                return
            }
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppSettings(context) // Fallback if the specific setting is unavailable
        }
    }

    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}