package com.example.sleep.library

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import org.altbeacon.beacon.*

class BeaconController(
    private val context: Activity,
    private val region: Region
) {
    companion object {
        private const val TAG = "RangingActivity"
        private const val DEFAULT_FOREGROUND_SCAN_PERIOD = 1000L
    }

    private var beacon: Beacon? = null
    private var beaconManager: BeaconManager = BeaconManager.getInstanceForApplication(context)
    private var beaconTransmitter: BeaconTransmitter? = null
    private var beaconParser: BeaconParser? = null

    private var beaconIsScanning = false
    private var beaconIsCasting = false

    private var dialogHelper: DialogHelper = DialogHelper(context)

    init {
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))
        beaconManager.foregroundScanPeriod = DEFAULT_FOREGROUND_SCAN_PERIOD

        beaconParser = BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
    }

    fun fixLollipop() {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        bluetoothManager.adapter.disable()
        Handler(Looper.getMainLooper()).postDelayed({
            bluetoothManager.adapter.enable()
        }, 2500)
    }

    fun startScanning(beaconModify: BeaconModify) {
        beaconManager.removeAllRangeNotifiers()
        beaconManager.addRangeNotifier(beaconModify::modifyData)
        beaconManager.startRangingBeacons(region)
        beaconIsScanning = true
    }

    fun stopScanning() {
        beaconManager.removeAllMonitorNotifiers()
        beaconManager.stopRangingBeacons(region)
        beaconManager.removeAllRangeNotifiers()
        beaconIsScanning = false
    }

    fun isScanning() :Boolean {
        return beaconIsScanning
    }

    fun broadcastBeacon(uuid: String, major: String, minor: String) {
        try {
            if (major.isNotBlank() && major != "null" && minor.isNotBlank() && minor != "null") {
                beacon = Beacon.Builder()
                    .setId1(uuid)
                    .setId2(major)
                    .setId3(minor)
                    .setManufacturer(0x0118)
                    .setTxPower(-69)
                    .setDataFields(listOf(0L))
                    .build()

                beaconTransmitter = BeaconTransmitter(context, beaconParser)
                beaconTransmitter!!.advertiseTxPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_HIGH
                beaconTransmitter!!.advertiseMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
            } else {
                dialogHelper.showDialog("錯誤", "請輸入正確的UUID、Major、Minor", true)
            }
        } catch (e: Exception) {
            dialogHelper.showDialog("錯誤", "請確認藍芽是否開啟", true, "確定", object :
                DialogHelper.OnPositiveListener {
                override fun onPositiveClick(dialogInterface: DialogInterface?, i: Int) {
                    dialogInterface?.dismiss()
                }
            })
        }
    }

    fun startBeaconCasting() {
        if (!isBeaconCasting()) beaconTransmitter?.startAdvertising(beacon)
        beaconIsCasting = true
    }

    fun stopBeaconCasting() {
        if (beaconIsCasting) beaconTransmitter?.stopAdvertising()
        beaconIsCasting = false
    }

    private fun isBeaconCasting(): Boolean {
        return beaconIsCasting
    }

    interface BeaconModify {
        fun modifyData(beacons: Collection<Beacon?>?, region: Region?)
    }
}