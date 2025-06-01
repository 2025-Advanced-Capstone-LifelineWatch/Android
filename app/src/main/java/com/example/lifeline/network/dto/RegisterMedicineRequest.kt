package com.example.lifeline.network.dto

data class RegisterMedicineRequest(
    val medicineName: String?,
    val repeatCycle: String,
    val medicineNote: String?,
    val times: List<String>,
    val dosage: List<Double>
)
