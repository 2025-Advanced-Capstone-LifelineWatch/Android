package com.example.lifeline.ui.monitor

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.lifeline.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate
import java.util.*

class BloodPressureActivity : AppCompatActivity() {

    private lateinit var chart: LineChart
    private lateinit var btnDate: Button
    private lateinit var viewModel: BloodPressureViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blood_pressure)

        chart = findViewById(R.id.blood_pressure_chart)
        btnDate = findViewById(R.id.btn_select_date)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this, BloodPressureViewModelFactory(this))[BloodPressureViewModel::class.java]

        btnDate.setOnClickListener {
            showDatePicker()
        }

        viewModel.hourlyAverages.observe(this) { list ->
            val sysEntries = list.mapIndexed { i, it -> Entry(i.toFloat(), it.avgSystolic.toFloat()) }
            val diaEntries = list.mapIndexed { i, it -> Entry(i.toFloat(), it.avgDiastolic.toFloat()) }

            val sysDataSet = LineDataSet(sysEntries, "수축기(mmHg)").apply {
                valueTextSize = 10f
            }
            val diaDataSet = LineDataSet(diaEntries, "이완기(mmHg)").apply {
                valueTextSize = 10f
            }

            chart.data = LineData(sysDataSet, diaDataSet)
            chart.invalidate()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val date = LocalDate.of(year, month + 1, day)
                viewModel.fetchAndAverageByDate(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}