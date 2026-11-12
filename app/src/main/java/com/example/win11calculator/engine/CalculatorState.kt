package com.example.win11calculator.engine

/**
 * Immutable snapshot of the calculator's current UI and operational state.
 *
 * @property display The current text shown on the primary result line
 * @property expression The text shown on the upper expression line (e.g., "12 + 8 =")
 * @property error Active calculation error if any occurred (e.g. divide by zero)
 * @property memory Current stored memory value, or null if memory is empty
 * @property history Chronological list of completed calculations
 */
data class CalculatorState(
    val display: String = "0",
    val expression: String = "",
    val error: CalculatorError? = null,
    val memory: Double? = null,
    val history: List<CalculationHistoryItem> = emptyList()
)
