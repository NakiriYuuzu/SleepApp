package com.example.sleep.presentation

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sleep.MainActivity
import com.example.sleep.R
import com.example.sleep.databinding.FragmentHomeBinding
import com.example.sleep.library.*
import com.example.sleep.util.Constant
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mqttHelper: MqttHelper
    private lateinit var dialogHelper: DialogHelper
    private lateinit var beaconController: BeaconController

    private var grant = ""
    private var currentUser = ""
    private var isOpenGate = false
    private var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        initView()
        initButton()
    }

    private fun initButton() {
        binding.button01.setOnClickListener {
            if (grant == "admin") {
                val message = "送貨員|您的貨物已經到達.|${System.currentTimeMillis()}"
                mqttHelper.publish(message)
                Toast.makeText(context, "已發送訊息", Toast.LENGTH_SHORT).show()
            } else {
                dialogHelper.showDialog("前往簽收畫面", "", true, "前往", "取消", object :
                    DialogHelper.OnDialogListener {
                    override fun onPositiveClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                        // TODO: SignUser
                        findNavController().navigate(R.id.action_homeFragment_to_drawFragment)
                    }

                    override fun onNegativeClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                    }
                })
            }
        }

        binding.button02.setOnClickListener {
            dialogHelper.showLoading()
            beaconController.startScanning(object : BeaconController.BeaconModify {
                override fun modifyData(beacons: Collection<Beacon?>?, region: Region?) {
                    if (!beacons.isNullOrEmpty()) {
                        Log.e("modifyData: ", beacons.first()?.id1.toString())
                        dialogHelper.hideLoading()
                        beaconController.stopScanning()
                        dialogHelper.showDialog("請問要打開貨櫃嗎？", "", false, "確定", "取消", object :
                            DialogHelper.OnDialogListener {
                            override fun onPositiveClick(dialog: DialogInterface?, which: Int) {
                                dialog?.dismiss()
                                isOpenGate = true
                                Log.e( "onPositiveClick: ", currentUser)
                                val message = if (grant == "admin") "送貨員|open|${System.currentTimeMillis()}" else "小花|open|${System.currentTimeMillis()}"
                                mqttHelper.publish(message)
                            }

                            override fun onNegativeClick(dialog: DialogInterface?, which: Int) {
                                dialog?.dismiss()
                            }
                        })

                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (count > 5) {
                                dialogHelper.hideLoading()
                                beaconController.stopScanning()
                                dialogHelper.showDialog("附近沒有beacon", "請稍候再嘗試...", false)
                                count = 0
                            }
                            count ++
                        }, 1000)
                    }
                }
            })
        }

        binding.button03.setOnClickListener {
            if (isOpenGate) {
                val message = if (grant == "admin") "送貨員|close|${System.currentTimeMillis()}" else "小花|close|${System.currentTimeMillis()}"
                mqttHelper.publish(message)
                isOpenGate = false
                Toast.makeText(context, "已發送訊息", Toast.LENGTH_SHORT).show()
            } else {
                dialogHelper.showDialog("貨櫃尚未開啟", "請先開啟貨櫃", false)
            }
        }
    }

    private fun initView() {
        dialogHelper = DialogHelper(requireActivity())
        dialogHelper.showLoading()
        beaconController = BeaconController(requireActivity(), Region("myRangingUniqueId", Identifier.parse(Constant.UUID), null, null))
        grant = ShareHelper(requireActivity()).get<String>(Constant.GRANT).toString()
        if (grant == "admin") {
            binding.animation.setAnimation(R.raw.admin2)
            binding.button01.text = "已抵達通知"
            binding.button02.text = "解鎖智能櫃"
            binding.button03.text = "上鎖智能櫃"
            currentUser = "送貨員"
        } else {
            binding.animation.setAnimation(R.raw.user)
            binding.button01.text = "簽收貨物"
            binding.button02.text = "解鎖智能櫃"
            binding.button03.text = "上鎖智能櫃"
            currentUser = "小花"
        }

        binding.animation.playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            mqttHelper = (activity as MainActivity).mqttHelper
            dialogHelper.hideLoading()
        }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}