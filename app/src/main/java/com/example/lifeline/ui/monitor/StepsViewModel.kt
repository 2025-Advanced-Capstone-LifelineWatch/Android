package com.example.lifeline.ui.monitor

import android.content.Context
import androidx.lifecycle.*
import com.example.lifeline.data.StepsData
import com.example.lifeline.util.HealthConnectManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class StepsViewModel(private val manager: HealthConnectManager) : ViewModel() {

    private val _stepsRecords = MutableLiveData<List<StepsData>>()
    val stepsRecords: LiveData<List<StepsData>> get() = _stepsRecords

    fun fetchByDate(date: LocalDate) {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val dailyStart = date.atStartOfDay(zoneId).toInstant()
            val endTime = date.plusDays(1).atStartOfDay(zoneId).toInstant()


            val all = manager.readSteps(dailyStart, endTime)

            val filtered = all.filter {
                val localDate = it.startTime.atZone(zoneId).toLocalDate()
                localDate == date
            }

            val records = filtered.map {
                StepsData(
                    time = it.startTime.atZone(zoneId).toString(),
                    count = it.count
                )
            }.sortedBy { it.time }

            _stepsRecords.value = records
        }
    }
}

class StepsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val manager = HealthConnectManager(context)
        return StepsViewModel(manager) as T
    }
}
