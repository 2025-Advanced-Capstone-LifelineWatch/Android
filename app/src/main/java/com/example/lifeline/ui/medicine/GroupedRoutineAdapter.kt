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
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.ui.medicine.dto.GroupedRoutineItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GroupedRoutineAdapter(
    private val items: List<GroupedRoutineItem>,
    private val onClick: (GroupedRoutineItem) -> Unit,
    private val scope: CoroutineScope
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

        holder.itemView.setOnClickListener {
            onClick(item)
        }

        item.times.forEachIndexed { index, time ->
            val context = holder.itemView.context
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_medicine_routine, holder.container, false)

            val tvName = view.findViewById<TextView>(R.id.tvMedicineName)
            val tvDosage = view.findViewById<TextView>(R.id.tvDosage)
            val cbTaken = view.findViewById<CheckBox>(R.id.cbTaken)

            tvName.text = item.medicineName

            val dose = item.dosage.getOrNull(index) ?: 1.0
            tvDosage.text = if (dose % 1 == 0.0) {
                "${dose.toInt()}정"
            } else {
                "${dose}정"
            }

            val alarmId = item.alarmIds.getOrNull(index)
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

            holder.container.addView(view)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun markAsCompleted(context: Context, alarmId: Long) {
        val prefs = context.getSharedPreferences("completed_alarms", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(alarmId.toString(), true).apply()
    }

    private fun isCompleted(context: Context, alarmId: Long): Boolean {
        val prefs = context.getSharedPreferences("completed_alarms", Context.MODE_PRIVATE)
        return prefs.getBoolean(alarmId.toString(), false)
    }
}
