package com.example.lifeline.ui.monitor

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.lifeline.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDate
import java.util.*

class BloodPressureActivity : AppCompatActivity() {

    private lateinit var chart: LineChart
    private lateinit var btnDate: Button
    private lateinit var tvDate: TextView
    private lateinit var summaryContainer: LinearLayout
    private lateinit var viewModel: BloodPressureViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_blood_pressure)

        chart = findViewById(R.id.blood_pressure_chart)
        btnDate = findViewById(R.id.btn_select_date)
        tvDate = findViewById(R.id.tv_selected_date)
        summaryContainer = findViewById(R.id.pressure_container)

        viewModel = ViewModelProvider(this, BloodPressureViewModelFactory(this))[BloodPressureViewModel::class.java]

        setupChart()

        btnDate.setOnClickListener {
            showDatePicker()
        }

        val today = LocalDate.now()
        tvDate.text = "${today.monthValue}.${today.dayOfMonth} (${getKoreanDayOfWeek(today)})"
        viewModel.fetchAndAverageByDate(today)


        viewModel.hourlyAverages.observe(this) { list ->
            val entries = list.mapIndexed { i, it ->
                Entry(i.toFloat(), it.avgSystolic.toFloat())
            }

            val dataSet = LineDataSet(entries, "수축기(mmHg)").apply {
                color = Color.parseColor("#EC407A")
                valueTextSize = 0f
                lineWidth = 2f
                setDrawCircles(false)
                setDrawFilled(true)
                fillColor = Color.parseColor("#F8BBD0")
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            val xLabels = list.map { it.time.substring(11, 16) }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
            chart.data = LineData(dataSet)
            chart.invalidate()

            summaryContainer.removeAllViews()
            list.forEach {
                val time = it.time.substring(11, 16)
                val hour = time.substring(0, 2)
                val minute = time.substring(3, 5)
                val row = TextView(this).apply {
                    text = "${hour}시 ${minute}분 : ${it.avgDiastolic.toInt()} (이완) / ${it.avgSystolic.toInt()} (수축)"
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    setPadding(0, 12, 0, 4)
                }
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                    ).apply { topMargin = 8 }
                    setBackgroundColor(Color.LTGRAY)
                }
                summaryContainer.addView(row)
                summaryContainer.addView(divider)
            }
        }
    }

    private fun setupChart() {
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setScaleEnabled(false)
            axisRight.isEnabled = false

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 200f
                granularity = 20f
                setLabelCount(11, true)
                setDrawGridLines(true)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)

            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 20f
                setDrawGridLines(false)
                textColor = Color.DKGRAY
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                tvDate.text = "${selectedMonth + 1}.${selectedDay} (${getKoreanDayOfWeek(date)})"
                viewModel.fetchAndAverageByDate(date)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun getKoreanDayOfWeek(date: LocalDate): String {
        return when (date.dayOfWeek.value) {
            1 -> "월"
            2 -> "화"
            3 -> "수"
            4 -> "목"
            5 -> "금"
            6 -> "토"
            else -> "일"
        }
    }
}
