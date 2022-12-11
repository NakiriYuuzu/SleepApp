package com.example.sleep.presentation

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.sleep.MainActivity
import com.example.sleep.R
import com.example.sleep.databinding.FragmentDrawBinding
import com.example.sleep.library.DialogHelper
import com.example.sleep.library.MqttHelper

class DrawFragment: Fragment(R.layout.fragment_draw) {

    private var _binding: FragmentDrawBinding? = null
    private val binding get() = _binding!!

    private lateinit var mqttHelper: MqttHelper
    private lateinit var dialogHelper: DialogHelper

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDrawBinding.bind(view)

        mqttHelper = (activity as MainActivity).mqttHelper
        dialogHelper = DialogHelper(requireActivity())

        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl("https://sketch.io/sketchpad/")

        binding.finishButton.setOnClickListener {
            dialogHelper.showDialog("簽收完成", "", false, "確定", object :
                DialogHelper.OnPositiveListener {
                override fun onPositiveClick(dialogInterface: DialogInterface?, i: Int) {
                    dialogInterface?.dismiss()
                    requireActivity().onBackPressed()
                    mqttHelper.publish("小花|camera|${System.currentTimeMillis()}")
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}