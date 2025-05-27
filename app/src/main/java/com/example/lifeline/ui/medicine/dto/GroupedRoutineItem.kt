package com.example.lifeline.ui.medicine.dto

data class GroupedRoutineItem(
    val groupId: Long,
    val medicineName: String,
    val repeatCycle: String,
    val medicineNote: String,
    val times: List<String>,     // "오전 9:00" 등 표시용
    val rawTimes: List<String>   // "09:00" 등 서버용
)