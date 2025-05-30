package com.example.lifeline.ui.medicine

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lifeline.R
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.network.dto.RegisterMedicineRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegisterMedicineActivity : AppCompatActivity() {

    private lateinit var inputMedicineName: EditText
    private lateinit var inputAlias: EditText
    private lateinit var tvRepeatCycle: TextView
    private lateinit var tvCount: TextView
    private lateinit var btnMinus: Button
    private lateinit var btnPlus: Button
    private lateinit var timeContainer: LinearLayout
    private lateinit var inputNote: EditText
    private lateinit var btnRegister: Button

    private var count = 1
    private val repeatOptions = listOf("한 번만", "매일", "이틀에 한 번", "일주일")
    private var selectedRepeatCycle = "매일" // 기본값

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_register_medicine)

        inputMedicineName = findViewById(R.id.inputMedicineName)
        tvRepeatCycle = findViewById(R.id.tvRepeatCycle)
        tvCount = findViewById(R.id.tvCount)
        btnMinus = findViewById(R.id.btnMinus)
        btnPlus = findViewById(R.id.btnPlus)
        timeContainer = findViewById(R.id.timeContainer)
        inputNote = findViewById(R.id.inputNote)
        btnRegister = findViewById(R.id.btnRegister)

        tvCount.text = count.toString()
        renderTimePickers()

        inputMedicineName.addTextChangedListener(textWatcher)

        btnPlus.setOnClickListener {
            if (count < 10) {
                count++
                tvCount.text = count.toString()
                renderTimePickers()
            }
        }

        btnMinus.setOnClickListener {
            if (count > 1) {
                count--
                tvCount.text = count.toString()
                renderTimePickers()
            }
        }

        tvRepeatCycle.setOnClickListener {
            showRepeatCyclePicker()
        }

        btnRegister.setOnClickListener {
            val name = inputMedicineName.text.toString().ifBlank { null }
            val note = inputNote.text.toString().ifBlank { null }

            val times = mutableListOf<String>()
            var dosage: Double? = null

            for (i in 0 until timeContainer.childCount) {
                val itemView = timeContainer.getChildAt(i)
                val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
                val tvDose = itemView.findViewById<TextView>(R.id.tvDose)

                val formatted = try {
                    val sdf = SimpleDateFormat("a h:mm", Locale.KOREAN)
                    val parsed = sdf.parse(tvTime.text.toString())
                    SimpleDateFormat("HH:mm", Locale.KOREAN).format(parsed!!)
                } catch (e: Exception) {
                    null
                }
                formatted?.let { times.add(it) }

                // 첫 번째 복용량만 추출
                if (dosage == null) {
                    dosage = tvDose.text.toString().replace("알", "").toDoubleOrNull() ?: 1.0
                }
            }

            if (times.isEmpty()) {
                Toast.makeText(this, "복용 시간을 설정해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val repeatCycleCode = when (selectedRepeatCycle) {
                "한 번만" -> "ONCE"
                "매일" -> "DAILY"
                "이틀에 한 번" -> "EVERY_OTHER_DAY"
                "일주일" -> "WEEKLY"
                else -> "DAILY"
            }

            val request = RegisterMedicineRequest(
                medicineName = name,
                repeatCycle = repeatCycleCode,
                medicineNote = note,
                times = times,
                dosage = dosage!!
            )

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.registerMedicineGroup(request)
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterMedicineActivity, "등록 성공!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterMedicineActivity, "등록 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterMedicineActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun renderTimePickers() {
        timeContainer.removeAllViews()
        repeat(count) {
            val itemView = layoutInflater.inflate(R.layout.item_time_dose, null)

            val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
            val tvDose = itemView.findViewById<TextView>(R.id.tvDose)

            tvTime.text = "오전 9:00"
            tvDose.text = "1알"

            tvTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                        }
                        val formattedTime = DateFormat.format("a h:mm", cal).toString()
                        tvTime.text = formattedTime
                    },
                    9, 0, false
                ).show()
            }

            tvDose.setOnClickListener {
                val doses = arrayOf("1알", "1.5알", "2알", "2.5알", "3알", "3.5알", "4알", "4.5알", "5알")
                AlertDialog.Builder(this)
                    .setTitle("복용량 선택")
                    .setItems(doses) { _, which ->
                        tvDose.text = doses[which]
                    }.show()
            }

            timeContainer.addView(itemView)
        }
    }

    private fun showRepeatCyclePicker() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_repeat_cycle_picker, null)
        val picker = view.findViewById<NumberPicker>(R.id.numberPicker)

        picker.minValue = 0
        picker.maxValue = repeatOptions.size - 1
        picker.displayedValues = repeatOptions.toTypedArray()
        picker.wrapSelectorWheel = false

        view.findViewById<Button>(R.id.btnSelect).setOnClickListener {
            selectedRepeatCycle = repeatOptions[picker.value]
            tvRepeatCycle.text = "섭취 요일: $selectedRepeatCycle"
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateRegisterButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun updateRegisterButtonState() {
        val nameFilled = inputMedicineName.text.toString().trim().isNotEmpty()
        btnRegister.isEnabled = nameFilled
        btnRegister.setTextColor(if (nameFilled) Color.WHITE else Color.BLACK)
    }
}
