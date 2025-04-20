package edu.msoe.myapplication.data

import java.time.LocalDate

data class ExportSummary(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalHours: Double,
    val totalTasks: Int
)
