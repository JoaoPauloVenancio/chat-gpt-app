package com.example.chatapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemChatAppBinding

class ChatAdapter :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val messageList = ArrayList<Message>()

    inner class ViewHolder(val binding: ItemChatAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        with(holder.binding) {
            if (message.sentBy == Message.SENT_BY_BOT) {
                chatGptMessage.isVisible = false
                providerMessage.isVisible = true
                txtProviderMessage.text = message.message
            } else {
                providerMessage.isVisible = false
                chatGptMessage.isVisible = true
                txtChatGptMessage.text = message.message
            }
        }
    }

    fun updateList(newList: List<Message>) {
        messageList.clear()
        messageList.addAll(newList)
        notifyDataSetChanged()
    }
}