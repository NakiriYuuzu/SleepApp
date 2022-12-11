package com.example.sleep.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sleep.NotifyAdapter
import com.example.sleep.R
import com.example.sleep.databinding.FragmentNotifyBinding
import com.example.sleep.library.MqttModel
import com.example.sleep.library.ShareHelper
import com.example.sleep.util.Constant
import org.json.JSONArray

class NotifyFragment : Fragment(R.layout.fragment_notify) {

    private var _binding: FragmentNotifyBinding? = null
    private val binding get() = _binding!!

    private lateinit var shareHelper: ShareHelper
    private lateinit var notifyAdapter: NotifyAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotifyBinding.bind(view)

        initView()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            hasFixedSize()
            adapter = notifyAdapter
        }
    }

    private fun initView() {
        shareHelper = ShareHelper(requireContext())
        notifyAdapter = NotifyAdapter()

        val grant = shareHelper.get<String>(Constant.GRANT)
        if (grant == "admin") {
            binding.title.text = "操作記錄"
            val data = shareHelper.get<List<MqttModel>>(Constant.ADMIN_DATA)?.toList()
            val list = arrayListOf<MqttModel>()
            val jsonArray = JSONArray(data)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val title = jsonObject.getString("title")
                val sender = jsonObject.getString("sender")
                val time = jsonObject.getString("time")
                val mqttModel = MqttModel(title, sender, time)
                list.add(mqttModel)
            }

            Log.e("TAG", "initView: " + data.toString() + " " + list.toString())

            notifyAdapter.differ.submitList(list)
        } else {
            val data = shareHelper.get<List<MqttModel>>(Constant.USER_DATA)?.toList()
            val list = arrayListOf<MqttModel>()
            val jsonArray = JSONArray(data)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val title = jsonObject.getString("title")
                val sender = jsonObject.getString("sender")
                val time = jsonObject.getString("time")
                val mqttModel = MqttModel(title, sender, time)
                list.add(mqttModel)
            }

            list.reverse()

            Log.e("TAG", "initView: " + data.toString() + " " + list.toString())

            notifyAdapter.differ.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}