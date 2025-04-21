package com.example.lifeline.data

data class BloodPressureData(
    val time: String,  // 시간 (HH:mm 포맷)
    val avgSystolic: Double, // 수축기 혈압 평균값
    val avgDiastolic: Double // 이완기 혈압 평균값
)
