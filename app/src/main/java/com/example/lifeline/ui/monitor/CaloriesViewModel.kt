package com.example.lifeline.ui.monitor

import android.content.Context
import androidx.lifecycle.*
import com.example.lifeline.data.CaloriesData
import com.example.lifeline.util.HealthConnectManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class CaloriesViewModel(
    private val manager: HealthConnectManager
) : ViewModel() {

    private val _caloriesRecords = MutableLiveData<List<CaloriesData>>()
    val caloriesRecords: LiveData<List<CaloriesData>> get() = _caloriesRecords

    fun fetchCaloriesByDate(date: LocalDate) {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val dailyStart = date.atStartOfDay(zoneId).toInstant()
            val endTime = date.plusDays(1).atStartOfDay(zoneId).toInstant()

            val all = manager.readCalories(dailyStart, endTime)

            val filtered = all.filter {
                val localDate = it.startTime.atZone(zoneId).toLocalDate()
                localDate == date
            }

            val records = filtered.map { record ->
                CaloriesData(
                    time = record.startTime.atZone(zoneId).toString(),
                    kcal = record.energy.inKilocalories
                )
            }.sortedBy { it.time }

            _caloriesRecords.value = records
        }
    }

}

class CaloriesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val manager = HealthConnectManager(context)
        return CaloriesViewModel(manager) as T
    }
}
