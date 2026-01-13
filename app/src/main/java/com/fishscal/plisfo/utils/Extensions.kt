package com.fishscal.plisfo.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateString(pattern: String = "MMM dd, yyyy"): String {
    val format = SimpleDateFormat(pattern, Locale.ENGLISH)
    return format.format(Date(this))
}

fun Float.toWeightString(unit: String = "kg"): String {
    return when (unit) {
        "kg" -> String.format("%.2f kg", this)
        "lb" -> String.format("%.2f lb", this * 2.20462f)
        else -> String.format("%.2f kg", this)
    }
}

fun Float.toLengthString(unit: String = "cm"): String {
    return when (unit) {
        "cm" -> String.format("%.1f cm", this)
        "in" -> String.format("%.1f in", this * 0.393701f)
        else -> String.format("%.1f cm", this)
    }
}

