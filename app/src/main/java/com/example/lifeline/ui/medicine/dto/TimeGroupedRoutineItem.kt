package com.example.lifeline.ui.medicine.dto

import com.example.lifeline.data.medicine.RoutineItem

data class RoutineGroup(
    val time: String,
    val medicines: List<RoutineItem>,
    val alarmId: Long? = null
)