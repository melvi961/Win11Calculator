package com.example.win11calculator.engine

/**
 * Represents a single calculation entry stored in history.
 *
 * @property expression The full mathematical expression (e.g., "12 + 8 =")
 * @property result The computed result of the expression (e.g., "20")
 * @property timestamp Epoch millisecond timestamp when the calculation occurred
 */
data class CalculationHistoryItem(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
