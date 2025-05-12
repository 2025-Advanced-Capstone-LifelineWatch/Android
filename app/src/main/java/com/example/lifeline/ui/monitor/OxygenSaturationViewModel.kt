package com.example.lifeline.ui.monitor

import android.content.Context
import androidx.lifecycle.*
import com.example.lifeline.data.OxygenSaturationData
import com.example.lifeline.util.HealthConnectManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class OxygenSaturationViewModel(
    private val manager: HealthConnectManager
) : ViewModel() {

    private val _records = MutableLiveData<List<OxygenSaturationData>>()
    val records: LiveData<List<OxygenSaturationData>> get() = _records

    fun fetchByDate(date: LocalDate) {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val startTime = date.atStartOfDay(zoneId).toInstant()
            val endTime = date.plusDays(1).atStartOfDay(zoneId).toInstant()

            val all = manager.readOxygenSaturation(startTime, endTime)

            val filtered = all.filter {
                val localDate = it.time.atZone(zoneId).toLocalDate()
                localDate == date
            }

            val records = filtered.map { record ->
                OxygenSaturationData(
                    time = record.time.atZone(zoneId).toString(),
                    saturation = record.percentage.value
                )
            }.sortedBy { it.time }

            _records.value = records
        }
    }

}

class OxygenSaturationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val manager = HealthConnectManager(context)
        return OxygenSaturationViewModel(manager) as T
    }
}
