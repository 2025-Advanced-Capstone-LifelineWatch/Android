package com.example.lifeline.network.dto

data class UpdateAlarmGroupRequest(
    val medicineName: String?,
    val repeatCycle: String,
    val medicineNote: String?,
    val times: List<String>
)
