package com.example.lifeline.ui.monitor

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.lifeline.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDate
import java.util.*

class RespirationActivity : AppCompatActivity() {

    private lateinit var chart: LineChart
    private lateinit var btnDate: Button
    private lateinit var tvDate: TextView
    private lateinit var container: LinearLayout
    private lateinit var viewModel: RespirationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_respiration)

        chart = findViewById(R.id.respiration_chart)
        btnDate = findViewById(R.id.btn_select_date)
        tvDate = findViewById(R.id.tv_selected_date)
        container = findViewById(R.id.respiration_container)

        viewModel = ViewModelProvider(this, RespirationViewModelFactory(this))[RespirationViewModel::class.java]

        setupChart()

        val today = LocalDate.now()
        tvDate.text = "${today.monthValue}.${today.dayOfMonth} (${getKoreanDayOfWeek(today)})"
        viewModel.fetchByDate(today)

        btnDate.setOnClickListener {
            showDatePicker()
        }

        viewModel.records.observe(this) { list ->
            val entries = list.mapIndexed { i, it ->
                Entry(i.toFloat(), it.bpm.toFloat())
            }

            val dataSet = LineDataSet(entries, "호흡 수 (bpm)").apply {
                color = Color.parseColor("#66BB6A")
                valueTextSize = 0f
                lineWidth = 2f
                setDrawCircles(false)
                setDrawFilled(true)
                fillColor = Color.parseColor("#C8E6C9")
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(list.map { it.time.substring(11, 16) })
            chart.data = LineData(dataSet)
            chart.invalidate()

            container.removeAllViews()
            list.forEach {
                val time = it.time.substring(11, 16)
                val (h, m) = time.split(":")
                val row = TextView(this).apply {
                    text = "${h}시 ${m}분 : ${String.format("%.1f", it.bpm)} 회"
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
                container.addView(row)
                container.addView(divider)
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
                axisMaximum = 50f
                granularity = 5f
                setLabelCount(11, true)
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

        DatePickerDialog(this, { _, y, m, d ->
            val date = LocalDate.of(y, m + 1, d)
            tvDate.text = "${m + 1}.${d} (${getKoreanDayOfWeek(date)})"
            viewModel.fetchByDate(date)
        }, year, month, day).show()
    }

    private fun getKoreanDayOfWeek(date: LocalDate): String {
        return listOf("월", "화", "수", "목", "금", "토", "일")[date.dayOfWeek.value - 1]
    }
}
