package com.example.lifeline.ui.medicine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.data.medicine.RoutineItem
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.ui.medicine.dto.GroupedRoutineItem
import com.example.lifeline.ui.medicine.dto.RoutineGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RoutineAdapter(
    private val items: List<RoutineGroup>,
    private val onItemClick: (RoutineItem) -> Unit,
    private val scope: CoroutineScope // ✅ CoroutineScope 추가
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    inner class RoutineViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val groupedContainer: LinearLayout = view.findViewById(R.id.groupedContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grouped_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val group = items[position]
        holder.tvTime.text = group.time
        holder.groupedContainer.removeAllViews()

        for (item in group.medicines) {
            val context = holder.view.context
            val itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_medicine_routine, holder.groupedContainer, false)

            val tvMedicineName = itemView.findViewById<TextView>(R.id.tvMedicineName)
            val tvDosage = itemView.findViewById<TextView>(R.id.tvDosage)
            val cbTaken = itemView.findViewById<CheckBox>(R.id.cbTaken)

            tvMedicineName.text = item.name
            tvDosage.text = item.dose

            val alarmId = item.alarmId

            if (alarmId != null) {
                val isAlreadyCompleted = isCompleted(context, alarmId)
                cbTaken.setOnCheckedChangeListener(null)
                cbTaken.isChecked = isAlreadyCompleted
                cbTaken.isEnabled = !isAlreadyCompleted

                cbTaken.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        scope.launch {
                            try {
                                val response = RetrofitClient.apiService.completeAlarm(alarmId)
                                if (response.isSuccessful) {
                                    markAsCompleted(context, alarmId)
                                    cbTaken.isEnabled = false
                                    Toast.makeText(context, "복용 완료!", Toast.LENGTH_SHORT).show()
                                } else {
                                    cbTaken.isChecked = false
                                    Toast.makeText(context, "복용 처리 실패", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                cbTaken.isChecked = false
                                Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }

            holder.groupedContainer.addView(itemView)
        }
    }

    private fun markAsCompleted(context: Context, alarmId: Long) {
        val prefs = context.getSharedPreferences("completed_alarms", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(alarmId.toString(), true).apply()
    }

    private fun isCompleted(context: Context, alarmId: Long): Boolean {
        val prefs = context.getSharedPreferences("completed_alarms", Context.MODE_PRIVATE)
        return prefs.getBoolean(alarmId.toString(), false)
    }
}


