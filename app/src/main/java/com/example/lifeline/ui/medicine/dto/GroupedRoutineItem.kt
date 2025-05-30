package com.example.lifeline.ui.medicine.dto

data class GroupedRoutineItem(
    val groupId: Long,
    val medicineName: String,
    val repeatCycle: String,
    val medicineNote: String,
    val times: List<String>,
    val rawTimes: List<String>,
    val alarmIds: List<Long>,
    val dosage: Double
)
