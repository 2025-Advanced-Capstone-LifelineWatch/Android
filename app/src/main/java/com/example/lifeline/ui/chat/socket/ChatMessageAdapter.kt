package com.example.lifeline.ui.chat.socket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifeline.R
import com.example.lifeline.data.chat.ChatMessage
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChatMessageAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ME = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isMe) VIEW_TYPE_ME else VIEW_TYPE_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_ME) {
            val view = inflater.inflate(R.layout.item_chat_me, parent, false)
            MyMessageViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_chat_other, parent, false)
            OtherMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val formattedTime = formatToKoreanTime(message.createdAt)

        if (holder is MyMessageViewHolder) {
            holder.tvMessage.text = message.message
            holder.tvTime.text = formattedTime
        } else if (holder is OtherMessageViewHolder) {
            holder.tvSenderName.text = message.senderName
            holder.tvMessage.text = message.message
            holder.tvTime.text = formattedTime
            Glide.with(holder.itemView.context)
                .load(message.profileImageUrl ?: "")
                .placeholder(R.drawable.ic_placeholder)
                .circleCrop()
                .into(holder.ivProfile)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    private fun formatToKoreanTime(isoTime: String?): String {
        return try {
            val time = OffsetDateTime.parse(isoTime)
            val formatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)
            time.format(formatter).replace("AM", "오전").replace("PM", "오후")
        } catch (e: Exception) {
            ""
        }
    }

    class MyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tv_me_message)
        val tvTime: TextView = view.findViewById(R.id.tv_me_time)
    }

    class OtherMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProfile: ImageView = view.findViewById(R.id.iv_profile)
        val tvSenderName: TextView = view.findViewById(R.id.tv_sender_name)
        val tvMessage: TextView = view.findViewById(R.id.tv_other_message)
        val tvTime: TextView = view.findViewById(R.id.tv_other_time)
    }

    fun setMessages(messages: List<ChatMessage>) {
        this.messages.clear()
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }
}
