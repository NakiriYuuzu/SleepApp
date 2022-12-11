package com.example.sleep.library

import android.app.Activity
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.widget.Toast

class PermissionHelper(private val activity: Activity) {

    private fun checkInternet(): Boolean {
        // register activity with the connectivity manager service
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection

        // Returns a Network object corresponding to
        // the currently active default data network.
        val network = connectivityManager.activeNetwork ?: return false

        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Indicates this network uses a Wi-Fi transport,
            // or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Indicates this network uses a Cellular transport. or
            // Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    }

    fun checkBluetooth():Boolean {
        @Suppress("DEPRECATION")
        val bluetoothManager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val blueToothAdapter = bluetoothManager.adapter

        if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(activity, "該裝置不支援藍芽", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "該裝置不支援BLE藍牙", Toast.LENGTH_SHORT).show()
            return false
        }

        if (blueToothAdapter == null || !blueToothAdapter.isEnabled) {
            return false
        }

        return true
    }

    fun checkGPS(): Boolean {
        @Suppress("DEPRECATION")
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    fun checkALL() {
        val dialogHelper = DialogHelper(activity)
        val locationRequest = LocationRequest(activity)
        var result = ""

        if (!checkInternet()) {
            result += "Internet "
        }

        if (!checkBluetooth()) {
            result += "Bluetooth "
        }

        locationRequest.requestLocation()
        locationRequest.requestBluetooth()

        val results = result.split(" ")
        results.forEach {
            when (it) {
                "Internet" -> {
                    dialogHelper.showDialog("請打開網絡", "", false, "確定", object :
                        DialogHelper.OnPositiveListener {
                        override fun onPositiveClick(dialogInterface: DialogInterface?, i: Int) {
                            activity.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                            dialogInterface?.dismiss()
                        }
                    })
                }
                "Bluetooth" -> {
                    dialogHelper.showDialog("請打開藍芽", "", false, "確定", object :
                        DialogHelper.OnPositiveListener {
                        override fun onPositiveClick(dialogInterface: DialogInterface?, i: Int) {
                            activity.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            dialogInterface?.dismiss()
                        }
                    })
                }
            }
        }
    }
}