package com.example.sleep.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.sleep.LoginActivity
import com.example.sleep.R
import com.example.sleep.databinding.FragmentSettingBinding
import com.example.sleep.library.DialogHelper
import com.example.sleep.library.ShareHelper
import com.example.sleep.util.Constant

class SettingFragment : Fragment(R.layout.fragment_setting) {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var shareHelper: ShareHelper
    private lateinit var dialogHelper: DialogHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingBinding.bind(view)

        initView()
        initButton()
    }

    private fun initButton() {
        binding.backupNotify.setOnClickListener {
            shareHelper.remove(Constant.USER_DATA)
            shareHelper.remove(Constant.ADMIN_DATA)
            dialogHelper.showDialog("通知備份成功！", "路徑為：/storage/emulated/0/Android/data/com.example.sleep/files/backup/notify.json", true)
        }

        binding.logout.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun initView() {
        shareHelper = ShareHelper(requireContext())
        dialogHelper = DialogHelper(requireActivity())
        val grant = ShareHelper(requireActivity()).get<String>(Constant.GRANT)
        if (grant == "admin") {
            binding.userImage.setImageResource(R.drawable.admin)
            binding.settingUserIconName.text = "管理員"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}