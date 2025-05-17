package com.example.lifeline.ui.mypage.detailed.notice

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.data.mypage.NoticeItem

class NoticeAdapter(private val items: List<NoticeItem>) :
    RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText = itemView.findViewById<TextView>(R.id.noticeTitle)
        val dateText = itemView.findViewById<TextView>(R.id.noticeDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notice, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title
        holder.dateText.text = item.date

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NoticeDetailActivity::class.java).apply {
                putExtra("title", item.title)
                putExtra("date", item.date)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}
