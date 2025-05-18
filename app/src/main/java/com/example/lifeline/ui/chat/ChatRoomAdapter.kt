package com.example.lifeline.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.network.dto.ChatRoomItem
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ChatRoomAdapter(
    private val items: List<ChatRoomItem>,
    private val onItemClick: (ChatRoomItem) -> Unit
) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    inner class ChatRoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val message: TextView = view.findViewById(R.id.tv_message)
        val time: TextView = view.findViewById(R.id.tv_time)

        fun bind(item: ChatRoomItem) {
            name.text = item.receiverName
            message.text = item.lastMessage
            time.text = item.lastMessageAt?.let { formatTimeAgo(it) }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_room, parent, false)
        return ChatRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    private fun formatTimeAgo(isoTime: String?): String {
        if (isoTime.isNullOrBlank()) return ""

        return try {
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val messageTime = OffsetDateTime.parse(isoTime, formatter)
            val now = OffsetDateTime.now()
            val diff = ChronoUnit.MINUTES.between(messageTime, now)

            when {
                diff < 1 -> "방금 전"
                diff < 60 -> "${diff}분 전"
                diff < 1440 -> "${diff / 60}시간 전"
                else -> "${diff / 1440}일 전"
            }
        } catch (e: Exception) {
            ""
        }
    }
}

