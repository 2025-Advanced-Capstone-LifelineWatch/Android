package com.example.lifeline.ui.monitor

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import com.example.lifeline.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate

import java.util.*

class BloodPressureFragment : Fragment(R.layout.fragment_blood_pressure) {

    private val viewModel: BloodPressureViewModel by viewModels {
        BloodPressureViewModelFactory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val chart: LineChart = view.findViewById(R.id.blood_pressure_chart)
        val btnDate: Button = view.findViewById(R.id.btn_select_date)

        btnDate.setOnClickListener { showDatePicker() }

        viewModel.hourlyAverages.observe(viewLifecycleOwner) { list ->
            // avgSystolic과 avgDiastolic을 사용하여 차트에 표시
            val sysEntries = list.mapIndexed { i, it -> Entry(i.toFloat(), it.avgSystolic.toFloat()) }
            val diaEntries = list.mapIndexed { i, it -> Entry(i.toFloat(), it.avgDiastolic.toFloat()) }

            val sysDataSet = LineDataSet(sysEntries, "수축기(mmHg)").apply {
                valueTextSize = 10f
            }
            val diaDataSet = LineDataSet(diaEntries, "이완기(mmHg)").apply {
                valueTextSize = 10f
            }

            chart.data = LineData(sysDataSet, diaDataSet)
            chart.invalidate()  // 차트 갱신
        }

    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
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