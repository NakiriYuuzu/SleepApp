package com.example.sleep.library

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class LocationRequest(private val context: Activity) : MultiplePermissionsListener {

    private var dialogHelper: DialogHelper = DialogHelper(context)

    fun requestLocation() {
        Dexter.withContext(context)
            .withPermissions(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ).withListener(this)
            .check()
    }

    fun requestBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Dexter.withContext(context)
                .withPermissions(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_ADVERTISE
                ).withListener(this)
                .check()
        } else {
            Dexter.withContext(context)
                .withPermissions(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                ).withListener(this)
                .check()
        }
    }

    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
        p0.let {
            if (p0?.areAllPermissionsGranted() == false) {
                dialogHelper.showDialog("暫無權限", "請給予權限", false, "確定", object :
                    DialogHelper.OnPositiveListener {
                    override fun onPositiveClick(dialogInterface: DialogInterface?, i: Int) {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        dialogInterface?.dismiss()
                    }
                })
            }
        }
    }

    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
        p1?.continuePermissionRequest()
    }
}