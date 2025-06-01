package com.example.lifeline.ui.medicine

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.data.medicine.RoutineItem
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.ui.medicine.dto.RoutineGroup
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class MedicineRoutineActivity : AppCompatActivity() {

    private var selectedDate: LocalDate = LocalDate.now()
    private lateinit var tvToolbarDate: TextView
    private lateinit var btnCalendar: ImageView
    private lateinit var tvRoutineTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var addBtn: TextView

    private val detailActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadGroupedRoutines()
            }
        }

    private val registerActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadGroupedRoutines()
            }
        }

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
            registerActivityLauncher.launch(intent)
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
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
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
        loadGroupedRoutines()
    }

    private fun loadGroupedRoutines() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMedicineGroups()
                if (response.isSuccessful) {
                    val groups = response.body()?.results ?: emptyList()
                    val today = selectedDate
                    val routineItems = groups.flatMap { group ->
                        group.alarms
                            .filter { alarm ->
                                val alarmDateTime = try {
                                    LocalDateTime.parse(alarm.time)
                                } catch (e: Exception) {
                                    return@filter false
                                }
                                val alarmDate = alarmDateTime.toLocalDate()

                                when (alarm.repeatCycle) {
                                    "ONCE" -> alarmDate == today
                                    "DAILY" -> true
                                    "EVERY_OTHER_DAY" -> {
                                        val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(alarmDate, today)
                                        daysDiff >= 0 && daysDiff % 2 == 0L
                                    }
                                    "WEEKLY" -> alarmDate.dayOfWeek == today.dayOfWeek
                                    else -> false
                                }
                            }
                            .mapNotNull { alarm ->
                                val parsed = try {
                                    LocalDateTime.parse(alarm.time)
                                } catch (e: Exception) {
                                    return@mapNotNull null
                                }
                                val displayTime = parsed.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))
                                val dosageValue = alarm.dosage ?: return@mapNotNull null

                                RoutineItem(
                                    time = displayTime,
                                    name = alarm.medicineName,
                                    dose = if (dosageValue % 1 == 0.0) "${dosageValue.toInt()}정" else "${dosageValue}정",
                                    isTaken = alarm.completed,
                                    dosage = dosageValue,
                                    alarmId = alarm.alarmId
                                )
                            }
                    }

                    val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)

                    val grouped = routineItems.groupBy { it.time }
                        .map { (time, list) -> RoutineGroup(time, list) }
                        .sortedBy { runCatching { LocalTime.parse(it.time, timeFormatter) }.getOrNull() ?: LocalTime.MIDNIGHT }


                    recyclerView.adapter = RoutineAdapter(
                        grouped,
                        onItemClick = { item ->
                            val routineGroup = groups.find { group ->
                                group.alarms.any { alarm ->
                                    val alarmTime = try {
                                        LocalDateTime.parse(alarm.time)
                                    } catch (e: Exception) {
                                        null
                                    }
                                    val alarmDisplayTime = alarmTime?.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))
                                    alarm.medicineName == item.name && alarmDisplayTime == item.time
                                }
                            }

                            if (routineGroup != null) {
                                val intent = Intent(this@MedicineRoutineActivity, MedicineDetailActivity::class.java).apply {
                                    putExtra("groupId", routineGroup.groupId)
                                    putExtra("medicineName", item.name)
                                    putExtra("repeatCycle", routineGroup.repeatCycle)
                                    putExtra("medicineNote", routineGroup.medicineNote ?: "")
                                    putStringArrayListExtra("times", ArrayList(routineGroup.alarms.map { it.time }))
                                    putExtra("dosageList", ArrayList(routineGroup.alarms.map { it.dosage ?: 1.0 }))
                                }
                                detailActivityLauncher.launch(intent)
                            } else {
                                Toast.makeText(this@MedicineRoutineActivity, "불러오기 실패", Toast.LENGTH_SHORT).show()
                            }
                        },
                        scope = lifecycleScope
                    )
                } else {
                    Toast.makeText(this@MedicineRoutineActivity, "불러오기 실패", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MedicineRoutineActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
