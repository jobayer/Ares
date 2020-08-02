package com.jobayr.ares

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

class Home : Activity() {

    private lateinit var accessibilityService: AccessibilityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessibilityService =
            getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (isAccessibilityServiceEnabled()) lock()
            else openAccessibilityService()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAccessibilityServiceEnabled()) {
            finishAndRemoveTask()
            lock()
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        var result = false
        val accessibilityServiceInfoList: List<AccessibilityServiceInfo> =
            accessibilityService.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (serviceInfo in accessibilityServiceInfoList) {
            val enabledServiceInfo = serviceInfo.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName == packageName) {
                result = true
            }
        }
        return result
    }

    private fun openAccessibilityService() {
        val accessibilityServiceIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        accessibilityServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(accessibilityServiceIntent)
    }

    private fun lock() {
        startService(
            Intent(
                LockAccessibilityService.SERVICE_NAME,
                null,
                this,
                LockAccessibilityService::class.java
            )
        )
    }

}