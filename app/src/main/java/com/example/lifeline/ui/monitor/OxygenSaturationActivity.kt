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

class OxygenSaturationActivity : AppCompatActivity() {

    private lateinit var chart: LineChart
    private lateinit var btnDate: Button
    private lateinit var tvDate: TextView
    private lateinit var summaryContainer: LinearLayout
    private lateinit var viewModel: OxygenSaturationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_oxygen_saturation)

        chart = findViewById(R.id.oxygen_chart)
        btnDate = findViewById(R.id.btn_select_date)
        tvDate = findViewById(R.id.tv_selected_date)
        summaryContainer = findViewById(R.id.oxygen_container)

        viewModel = ViewModelProvider(this, OxygenSaturationViewModelFactory(this))[OxygenSaturationViewModel::class.java]

        setupChart()

        btnDate.setOnClickListener {
            showDatePicker()
        }

        val today = LocalDate.now()
        tvDate.text = "${today.monthValue}.${today.dayOfMonth} (${getKoreanDayOfWeek(today)})"
        viewModel.fetchByDate(today)


        viewModel.records.observe(this) { list ->
            val entries = list.mapIndexed { i, it ->
                Entry(i.toFloat(), it.saturation.toFloat())
            }

            val dataSet = LineDataSet(entries, "산소포화도 (%)").apply {
                color = Color.parseColor("#26C6DA")
                valueTextSize = 0f
                lineWidth = 2f
                setDrawCircles(false)
                setDrawFilled(true)
                fillColor = Color.parseColor("#B2EBF2")
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            val xLabels = list.map { it.time.substring(11, 16) }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
            chart.data = LineData(dataSet)
            chart.invalidate()

            summaryContainer.removeAllViews()
            list.forEach {
                val time = it.time.substring(11, 16)
                val (h, m) = time.split(":")
                val row = TextView(this).apply {
                    text = "${h}시 ${m}분 : ${it.saturation}%"
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
                axisMinimum = 60f
                axisMaximum = 100f
                granularity = 5f
                setLabelCount(9, true)
                setDrawGridLines(true)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
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
                viewModel.fetchByDate(date)
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
