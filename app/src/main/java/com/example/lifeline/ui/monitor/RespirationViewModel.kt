package com.example.lifeline.ui.monitor

import android.content.Context
import androidx.lifecycle.*
import com.example.lifeline.data.RespirationData
import com.example.lifeline.util.HealthConnectManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class RespirationViewModel(
    private val manager: HealthConnectManager
) : ViewModel() {

    private val _records = MutableLiveData<List<RespirationData>>()
    val records: LiveData<List<RespirationData>> get() = _records

    fun fetchByDate(date: LocalDate) {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val startTime = date.atStartOfDay(zoneId).toInstant()
            val endTime = date.plusDays(1).atStartOfDay(zoneId).toInstant()

            val all = manager.readRespiratoryRate(startTime, endTime)

            val filtered = all.filter {
                val localDate = it.time.atZone(zoneId).toLocalDate()
                localDate == date
            }

            val records = filtered.map { record ->
                RespirationData(
                    time = record.time.atZone(zoneId).toString(),
                    bpm = record.rate
                )
            }.sortedBy { it.time }

            _records.value = records
        }
    }
}

class RespirationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val manager = HealthConnectManager(context)
        return RespirationViewModel(manager) as T
    }
}
