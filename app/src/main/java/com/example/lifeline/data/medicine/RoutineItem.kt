package com.example.lifeline.data.medicine

data class RoutineItem(
    val time: String,
    val name: String,
    val dose: String,
    var isTaken: Boolean
)
