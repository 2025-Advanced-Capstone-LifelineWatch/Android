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
    val time: String,
    val medicineNote: String?,
    val completed: Boolean,
    val repeatCycle: String,
    val dosage: Double? // ✅ 추가!
)
