package com.example.lifeline.ui.medicine

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lifeline.R
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.network.dto.UpdateAlarmGroupRequest
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class MedicineDetailActivity : AppCompatActivity() {

    private lateinit var inputMedicineName: EditText
    private lateinit var tvRepeatCycle: TextView
    private lateinit var inputNote: EditText
    private lateinit var timeContainer: LinearLayout
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var tvCount: TextView
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button

    private var isEditMode = false
    private var groupId: Long = -1
    private var count = 1

    private val repeatOptions = listOf("한 번만", "매일", "이틀에 한 번", "일주일")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_medicine_detail)

        inputMedicineName = findViewById(R.id.inputMedicineName)
        tvRepeatCycle = findViewById(R.id.tvRepeatCycle)
        inputNote = findViewById(R.id.inputNote)
        timeContainer = findViewById(R.id.timeContainer)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDel)
        tvCount = findViewById(R.id.tvCount)
        btnPlus = findViewById(R.id.btnPlus)
        btnMinus = findViewById(R.id.btnMinus)

        val name = intent.getStringExtra("medicineName") ?: ""
        val repeatCycle = intent.getStringExtra("repeatCycle") ?: ""
        val note = intent.getStringExtra("medicineNote") ?: ""
        val times = intent.getStringArrayListExtra("times") ?: arrayListOf()
        groupId = intent.getLongExtra("groupId", -1)

        inputMedicineName.setText(name)
        tvRepeatCycle.text = "섭취 요일: ${convertRepeatCycleToKorean(repeatCycle)}"
        inputNote.setText(note)
        count = times.size
        tvCount.text = count.toString()
        renderTimePickers(times)

        setEditable(false)

        tvRepeatCycle.setOnClickListener {
            if (isEditMode) showRepeatCyclePicker()
        }

        btnPlus.setOnClickListener {
            if (isEditMode && count < 10) {
                count++
                tvCount.text = count.toString()
                renderTimePickers((0 until count).map { "09:00" })
            }
        }

        btnMinus.setOnClickListener {
            if (isEditMode && count > 1) {
                count--
                tvCount.text = count.toString()
                renderTimePickers((0 until count).map { "09:00" })
            }
        }

        btnEdit.setOnClickListener {
            if (isEditMode) {
                val updatedName = inputMedicineName.text.toString()
                val updatedNote = inputNote.text.toString()

                val timesFormatted = mutableListOf<String>()
                for (i in 0 until timeContainer.childCount) {
                    val view = timeContainer.getChildAt(i)
                    val tvTime = view.findViewById<TextView>(R.id.tvTime)
                    val timeText = tvTime.text.toString()
                    val parsed = try {
                        val time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))
                        time.format(DateTimeFormatter.ofPattern("HH:mm"))
                    } catch (e: Exception) {
                        null
                    }
                    parsed?.let { timesFormatted.add(it) }
                }

                val repeatCycleCode = when (tvRepeatCycle.text.toString().replace("섭취 요일: ", "")) {
                    "한 번만" -> "ONCE"
                    "매일" -> "DAILY"
                    "이틀에 한 번" -> "EVERY_OTHER_DAY"
                    "일주일" -> "WEEKLY"
                    else -> "DAILY"
                }

                val request = UpdateAlarmGroupRequest(
                    medicineName = updatedName,
                    repeatCycle = repeatCycleCode,
                    medicineNote = updatedNote.ifBlank { null },
                    times = timesFormatted
                )

                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.apiService.updateAlarmGroup(groupId, request)
                        if (response.isSuccessful) {
                            Toast.makeText(this@MedicineDetailActivity, "수정 완료", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@MedicineDetailActivity, "수정 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MedicineDetailActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                setEditable(true)
                btnEdit.text = "저장"
            }
            isEditMode = !isEditMode
        }

        btnDelete.setOnClickListener {
            Toast.makeText(this, "삭제 완료 (구현 필요)", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun convertRepeatCycleToKorean(code: String): String {
        return when (code) {
            "ONCE" -> "한 번만"
            "DAILY" -> "매일"
            "EVERY_OTHER_DAY" -> "이틀에 한 번"
            "WEEKLY" -> "일주일"
            else -> "매일" // 기본값
        }
    }

    private fun setEditable(editable: Boolean) {
        inputMedicineName.isEnabled = editable
        inputNote.isEnabled = editable
        tvRepeatCycle.isEnabled = editable
    }

    private fun renderTimePickers(times: List<String>) {
        timeContainer.removeAllViews()
        for (time in times) {
            val view = layoutInflater.inflate(R.layout.item_time_dose, null)
            val tvTime = view.findViewById<TextView>(R.id.tvTime)
            val tvDose = view.findViewById<TextView>(R.id.tvDose)

            val localTime = try {
                val parsed = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
                parsed.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))
            } catch (e: Exception) {
                time
            }

            tvTime.text = localTime
            tvDose.text = "1정"

            tvTime.setOnClickListener {
                if (!isEditMode) return@setOnClickListener
                val cal = Calendar.getInstance()
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                        }
                        val formatted = DateFormat.format("a h:mm", calendar).toString()
                        tvTime.text = formatted
                    },
                    9, 0, false
                ).show()
            }

            tvDose.setOnClickListener {
                if (!isEditMode) return@setOnClickListener
                val doses = arrayOf("1정", "1.5정", "2정", "2.5정", "3정", "3.5정", "4정", "4.5정", "5정")
                AlertDialog.Builder(this)
                    .setTitle("복용량 선택")
                    .setItems(doses) { _, which ->
                        tvDose.text = doses[which]
                    }.show()
            }

            timeContainer.addView(view)
        }
    }


    private fun showRepeatCyclePicker() {
        AlertDialog.Builder(this)
            .setTitle("섭취 주기 선택")
            .setItems(repeatOptions.toTypedArray()) { _, which ->
                tvRepeatCycle.text = "섭취 요일: ${repeatOptions[which]}"
            }
            .show()
    }
}
