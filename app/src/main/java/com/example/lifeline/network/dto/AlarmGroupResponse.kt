package com.example.lifeline.network.dto

data class AlarmGroupResponse(
    val results: List<AlarmGroup>
)

data class AlarmGroup(
    val groupId: Long,
    val medicineName: String,
    val medicineNote: String?,
    val repeatCycle: String,
    val alarms: List<Alarm>,
    val dosage: Double?
)

data class Alarm(
    val alarmId: Long,
    val elderlyId: Long,
    val medicineName: String,
    val time: String,  // "2025-05-18T12:00:00"
    val medicineNote: String?,
    val completed: Boolean,
    val repeatCycle: String
)
