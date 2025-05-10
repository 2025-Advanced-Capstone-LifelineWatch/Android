package com.example.lifeline.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R

class MyPageAdapter(
    private val items: List<MyPageItem>,
    private val itemClick: (MyPageItem) -> Unit
) : RecyclerView.Adapter<MyPageAdapter.MyPageViewHolder>() {

    inner class MyPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.ivIcon)
        val label: TextView = itemView.findViewById(R.id.tvLabel)
        val groupTitle: TextView = itemView.findViewById(R.id.tvGroupTitle)
        val itemLayout: LinearLayout = itemView.findViewById(R.id.itemLayout)

        fun bind(item: MyPageItem) {
            if (item.iconResId != null) {
                icon.visibility = View.VISIBLE
                icon.setImageResource(item.iconResId)
            } else {
                icon.visibility = View.GONE
            }

            label.text = item.text

            if (item.groupTitle != null) {
                groupTitle.text = item.groupTitle
                groupTitle.visibility = View.VISIBLE
            } else {
                groupTitle.visibility = View.GONE
            }

            itemLayout.setOnClickListener {
                itemClick(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_page, parent, false)
        return MyPageViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MyPageViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
