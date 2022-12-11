package com.example.sleep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sleep.databinding.ActivityMainBinding
import com.example.sleep.library.DialogHelper
import com.example.sleep.library.MqttHelper
import com.example.sleep.library.PermissionHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: FragmentContainerView
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var dialogHelper: DialogHelper
    lateinit var mqttHelper: MqttHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = findViewById(R.id.fragment)
        binding.bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        initView()
    }

    private fun initView() {
        mqttHelper = MqttHelper(this)
        dialogHelper = DialogHelper(this)
        permissionHelper = PermissionHelper(this)

        permissionHelper.checkALL()
        mqttHelper.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttHelper.disconnect()
    }
}