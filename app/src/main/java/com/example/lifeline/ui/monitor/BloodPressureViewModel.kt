package com.example.lifeline.ui.monitor

import android.content.Context
import androidx.lifecycle.*
import com.example.lifeline.data.BloodPressureData
import com.example.lifeline.util.HealthConnectManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class BloodPressureViewModel(
    private val manager: HealthConnectManager
) : ViewModel() {

    private val _hourlyAverages = MutableLiveData<List<BloodPressureData>>()
    val hourlyAverages: LiveData<List<BloodPressureData>> get() = _hourlyAverages

    fun fetchAndAverageByDate(date: LocalDate) {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val all = manager.readBloodPressure()

            val filtered = all.filter {
                val localDate = it.time.atZone(zoneId).toLocalDate()
                localDate == date
            }

            val records = filtered.map { record ->
                BloodPressureData(
                    time = record.time.atZone(zoneId).toString(),
                    avgSystolic = record.systolic.inMillimetersOfMercury,
                    avgDiastolic = record.diastolic.inMillimetersOfMercury
                )
            }.sortedBy { it.time }

            _hourlyAverages.value = records
        }
    }
}

class BloodPressureViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val manager = HealthConnectManager(context)
        return BloodPressureViewModel(manager) as T
    }
}
