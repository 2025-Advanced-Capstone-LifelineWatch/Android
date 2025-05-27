package com.example.lifeline.ui.medicine

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.network.dto.Alarm
import com.example.lifeline.network.dto.AlarmGroup
import com.example.lifeline.ui.medicine.dto.GroupedRoutineItem
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class MedicineRoutineActivity : AppCompatActivity() {

    private lateinit var tvToolbarDate: TextView
    private lateinit var btnCalendar: ImageView
    private lateinit var tvRoutineTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var addBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_medicine_main)

        tvToolbarDate = findViewById(R.id.tvToolbarDate)
        btnCalendar = findViewById(R.id.btnCalendar)
        tvRoutineTitle = findViewById(R.id.tvRoutineTitle)
        recyclerView = findViewById(R.id.rvMedicineRoutine)
        addBtn = findViewById(R.id.btnAdd)

        addBtn.setOnClickListener {
            val intent = Intent(this, RegisterMedicineActivity::class.java)
            startActivityForResult(intent, 100)
        }

        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userName = sharedPref.getString("name", "사용자") ?: "사용자"
        tvRoutineTitle.text = "${userName}님의 섭취 루틴"

        val today = LocalDate.now()
        updateDate(today)

        btnCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    updateDate(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        loadGroupedRoutines()
    }

    private fun updateDate(date: LocalDate) {
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
        val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + " ($dayOfWeek)"
        tvToolbarDate.text = formattedDate
    }

    private fun loadGroupedRoutines() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMedicineGroups()
                if (response.isSuccessful) {
                    val groups = response.body()?.results ?: emptyList()
                    val groupedItems = groups.map { group ->
                        val parsedTimes = group.alarms.mapNotNull {
                            try {
                                val parsed = LocalDateTime.parse(it.time)
                                parsed.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)) to
                                        parsed.format(DateTimeFormatter.ofPattern("HH:mm"))
                            } catch (e: Exception) {
                                null
                            }
                        }

                        GroupedRoutineItem(
                            groupId = group.groupId,
                            medicineName = group.medicineName,
                            repeatCycle = group.repeatCycle,
                            medicineNote = group.medicineNote ?: "",
                            times = parsedTimes.map { it.first },
                            rawTimes = parsedTimes.map { it.second },
                            alarmIds = group.alarms.mapNotNull { it.alarmId }
                        )
                    }
                    recyclerView.adapter = GroupedRoutineAdapter(
                        items = groupedItems,
                        onClick = { item ->
                            val intent = Intent(this@MedicineRoutineActivity, MedicineDetailActivity::class.java).apply {
                                putExtra("groupId", item.groupId)
                                putExtra("medicineName", item.medicineName)
                                putExtra("repeatCycle", item.repeatCycle)
                                putExtra("medicineNote", item.medicineNote)
                                putStringArrayListExtra("times", ArrayList(item.rawTimes))
                            }
                            startActivity(intent)
                        },
                        scope = lifecycleScope // ✅ CoroutineScope 전달
                    )
                } else {
                    Toast.makeText(this@MedicineRoutineActivity, "불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MedicineRoutineActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
