package com.example.fyp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.core.content.ContextCompat

fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}


inline fun Context.cameraPermissionRequest(crossinline positive: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle("Camera Permission Required")
        .setPositiveButton("Allow Camera") { dialog, which ->

        }.show()

}


fun Context.openPermissionSetting() {
    Intent(ACTION_APPLICATION_DETAILS_SETTINGS).also {
        val uri: Uri = Uri.fromParts("package", packageName, null)
        it.data = uri
        startActivity(it)
    }
}
