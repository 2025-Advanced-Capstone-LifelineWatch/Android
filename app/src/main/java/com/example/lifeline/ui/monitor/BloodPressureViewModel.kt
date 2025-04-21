package com.example.lifeline.ui.monitor

import android.content.Context
import androidx.lifecycle.*
import com.example.lifeline.data.BloodPressureData
import com.example.lifeline.util.HealthConnectManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class BloodPressureViewModel(
    private val manager: HealthConnectManager
) : ViewModel() {

    private val _hourlyAverages = MutableLiveData<List<BloodPressureData>>()
    val hourlyAverages: LiveData<List<BloodPressureData>> get() = _hourlyAverages

    fun fetchAndAverageByDate(date: LocalDate) {
        viewModelScope.launch {
            val all = manager.readBloodPressure()  // BloodPressureRecord 리스트 받기

            // 날짜 필터링
            val filtered = all.filter {
                val localDate = it.time.atZone(ZoneId.systemDefault()).toLocalDate() // it은 BloodPressureRecord 타입
                localDate == date
            }

            // 시간별로 평균 계산
            val averages = filtered.groupBy {
                it.time.truncatedTo(ChronoUnit.HOURS)
            }.map { (time, group) ->
                // 평균 계산
                val systolicAvg = group.map { it.systolic.inMillimetersOfMercury }.average() // systolic 값
                val diastolicAvg = group.map { it.diastolic.inMillimetersOfMercury }.average() // diastolic 값

                // BloodPressureData 객체 생성 (평균값을 사용)
                BloodPressureData(
                    time = time.toString(), // 시간 포맷 필요시 바꾸기
                    avgSystolic = systolicAvg,  // 수축기 평균값
                    avgDiastolic = diastolicAvg // 이완기 평균값
                )
            }.sortedBy { it.time }

            // 평균값 저장
            _hourlyAverages.value = averages
        }
    }


}

class BloodPressureViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val manager = HealthConnectManager(context)
        return BloodPressureViewModel(manager) as T
    }
}
