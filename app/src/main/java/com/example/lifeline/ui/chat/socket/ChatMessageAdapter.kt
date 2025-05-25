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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChatMessageAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DATE = 0
        private const val VIEW_TYPE_ME = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].isDateHeader -> VIEW_TYPE_DATE
            messages[position].isMe -> VIEW_TYPE_ME
            else -> VIEW_TYPE_OTHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_DATE -> {
                val view = inflater.inflate(R.layout.item_chat_date_header, parent, false)
                DateViewHolder(view)
            }
            VIEW_TYPE_ME -> {
                val view = inflater.inflate(R.layout.item_chat_me, parent, false)
                MyMessageViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_chat_other, parent, false)
                OtherMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val formattedTime = formatToKoreanTime(message.createdAt)

        when (holder) {
            is MyMessageViewHolder -> {
                holder.tvMessage.text = message.message
                holder.tvTime.text = formattedTime
            }
            is OtherMessageViewHolder -> {
                holder.tvSenderName.text = message.senderName
                holder.tvMessage.text = message.message
                holder.tvTime.text = formattedTime
                Glide.with(holder.itemView.context)
                    .load(message.profileImageUrl ?: "")
                    .placeholder(R.drawable.ic_placeholder)
                    .circleCrop()
                    .into(holder.ivProfile)
            }
            is DateViewHolder -> {
                holder.tvDate.text = formatToDate(message.createdAt)
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        val currentDate = try {
            OffsetDateTime.parse(message.createdAt).toLocalDate().toString()
        } catch (e: Exception) {
            message.createdAt.substring(0, 10) // fallback
        }
        val lastDate = try {
            messages.lastOrNull { !it.isDateHeader }
                ?.let { OffsetDateTime.parse(it.createdAt).toLocalDate().toString() }
        } catch (e: Exception) {
            messages.lastOrNull { !it.isDateHeader }?.createdAt?.substring(0, 10)
        }
        if (currentDate != lastDate) {
            messages.add(
                ChatMessage(
                    createdAt = message.createdAt,
                    isDateHeader = true
                )
            )
        }
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

//    private fun formatToKoreanTime(createdAt: String?): String {
//        return try {
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//            val parsed = LocalDateTime.parse(createdAt, formatter)
//            val hourMinuteFormat = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)
//            hourMinuteFormat.format(parsed)
//                .replace("AM", "오전")
//                .replace("PM", "오후")
//        } catch (e: Exception) {
//            ""
//        }
//    }
    private fun formatToKoreanTime(createdAt: String?): String {
        return try {
            // ISO_OFFSET_DATE_TIME 우선 시도
            val parsed = OffsetDateTime.parse(createdAt)
            val formatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)
            parsed.format(formatter)
                .replace("AM", "오전")
                .replace("PM", "오후")
        } catch (e1: Exception) {
            try {
                // 실패 시 yyyy-MM-dd HH:mm:ss 포맷 시도
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val parsed = LocalDateTime.parse(createdAt, formatter)
                val hourMinuteFormat = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)
                hourMinuteFormat.format(parsed)
                    .replace("AM", "오전")
                    .replace("PM", "오후")
            } catch (e2: Exception) {
                ""
            }
        }
    }


//    private fun formatToDate(createdAt: String?): String {
//        return try {
//            val parsedDate = LocalDate.parse(createdAt?.substring(0, 10))
//            parsedDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
//        } catch (e: Exception) {
//            "" //       }
//    }

    private fun formatToDate(createdAt: String?): String {
        return try {
            // ISO_OFFSET_DATE_TIME 우선 시도
            val parsedDate = OffsetDateTime.parse(createdAt).toLocalDate()
            parsedDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        } catch (e1: Exception) {
            try {
                val parsedDate = LocalDate.parse(createdAt?.substring(0, 10))
                parsedDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            } catch (e2: Exception) {
                ""
            }
        }
    }

    fun setMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        var lastDate: String? = null

        val sortedMessages = newMessages.sortedBy { it.createdAt }

        for (msg in sortedMessages) {
            val currentDate = msg.createdAt.substring(0, 10)
            if (currentDate != lastDate) {
                messages.add(
                    ChatMessage(
                        createdAt = msg.createdAt,
                        isDateHeader = true
                    )
                )
                lastDate = currentDate
            }
            messages.add(msg)
        }
        notifyDataSetChanged()
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

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tv_date_header)
    }
}
