package com.example.lifeline.ui.medicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.ui.medicine.dto.GroupedRoutineItem

class GroupedRoutineAdapter(
    private val items: List<GroupedRoutineItem>,
    private val onClick: (GroupedRoutineItem) -> Unit
) : RecyclerView.Adapter<GroupedRoutineAdapter.GroupedViewHolder>() {

    inner class GroupedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val container: LinearLayout = view.findViewById(R.id.groupedContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grouped_routine, parent, false)
        return GroupedViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupedViewHolder, position: Int) {
        val item = items[position]
        holder.tvTime.text = item.times.firstOrNull() ?: ""
        holder.container.removeAllViews()

        item.times.forEach { time ->
            val view = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_medicine_routine, holder.container, false)

            val tvName = view.findViewById<TextView>(R.id.tvMedicineName)
            val tvTime = view.findViewById<TextView>(R.id.tvDosage) // 재사용
            tvName.text = item.medicineName
            tvTime.text = time

            holder.container.addView(view)
        }

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
