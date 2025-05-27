package com.example.lifeline.ui.medicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.data.medicine.RoutineItem

class RoutineAdapter(
    private val routineList: List<RoutineItem>
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    inner class RoutineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvMedicineName: TextView = itemView.findViewById(R.id.tvMedicineName)
        val tvDosage: TextView = itemView.findViewById(R.id.tvDosage)
        val cbTaken: CheckBox = itemView.findViewById(R.id.cbTaken)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val item = routineList[position]

        holder.tvTime.text = item.time
        holder.tvMedicineName.text = item.name
        holder.tvDosage.text = item.dose
        holder.cbTaken.isChecked = item.isTaken

        holder.cbTaken.setOnCheckedChangeListener { _, isChecked ->
            item.isTaken = isChecked
            // TODO: 서버로 상태 전송이 필요하다면 이곳에서 처리
        }
    }

    override fun getItemCount(): Int = routineList.size
}

