package com.example.lifeline.ui.monitor

import android.content.Context
import androidx.lifecycle.*
import com.example.lifeline.data.TemperatureData
import com.example.lifeline.util.HealthConnectManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class TemperatureViewModel(
    private val manager: HealthConnectManager
) : ViewModel() {

    private val _dailyTemperatures = MutableLiveData<List<TemperatureData>>()
    val dailyTemperatures: LiveData<List<TemperatureData>> get() = _dailyTemperatures

    fun fetchTemperaturesByDate(date: LocalDate) {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val all = manager.readTemperature()

            val filtered = all.filter {
                val localDate = it.time.atZone(zoneId).toLocalDate()
                localDate == date
            }

            val records = filtered.map { record ->
                TemperatureData(
                    time = record.time.atZone(zoneId).toString(),
                    celsius = record.temperature.inCelsius
                )
            }.sortedBy { it.time }

            _dailyTemperatures.value = records
        }
    }
}

class TemperatureViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val manager = HealthConnectManager(context)
        return TemperatureViewModel(manager) as T
    }
}
