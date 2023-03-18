package com.astrash.gesturelab.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

class PackageUtils {
    companion object {
        fun getAppInfoList(packageManager: PackageManager): List<AppInfo> {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            return packageManager.queryIntentActivities(intent, 0).map {
                AppInfo(
                    label = it.loadLabel(packageManager),
                    packageName = it.activityInfo.packageName,
                    icon = it.activityInfo.loadIcon(packageManager)
                )
            }
        }
    }
}

data class AppInfo(val label: CharSequence, val packageName: CharSequence, val icon: Drawable) {
    fun launch(context: Context, packageManager: PackageManager) {
        packageManager.getLaunchIntentForPackage(packageName as String)?.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)?.also {
            context.startActivity(it)
        }
    }
}
