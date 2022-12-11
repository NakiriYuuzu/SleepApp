package com.example.sleep

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sleep.databinding.RecyclerNotifyBinding
import com.example.sleep.library.MqttModel

class NotifyAdapter: RecyclerView.Adapter<NotifyAdapter.NotifyHolder>() {

    inner class NotifyHolder(val binding: RecyclerNotifyBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<MqttModel>() {
        override fun areItemsTheSame(oldItem: MqttModel, newItem: MqttModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MqttModel, newItem: MqttModel): Boolean {
            if (oldItem.sender == newItem.sender) return true
            if (oldItem.time == newItem.time) return true
            if (oldItem.title == newItem.title) return true
            return false
        }
    }

    val differ = androidx.recyclerview.widget.AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyHolder {
        return NotifyHolder(RecyclerNotifyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotifyHolder, position: Int) {
        holder.binding.apply {
            val data = differ.currentList[position]
            title.text = data.title
            content.text = data.time
            sender.text = data.sender

            if (data.sender == "送貨員") senderImage.setImageResource(R.drawable.admin)
            else senderImage.setImageResource(R.drawable.user)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}