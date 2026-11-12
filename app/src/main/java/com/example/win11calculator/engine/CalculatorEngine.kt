package com.example.win11calculator.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.sqrt

/**
 * Pure Kotlin calculator engine matching Windows 11 Standard Calculator behavior.
 * Contains zero Android dependencies and zero third-party math libraries.
 */
class CalculatorEngine {

    // Current number displayed on the main readout
    private var display: String = "0"

    // Exact value behind the display when it came from a calculation (null = parse the typed text).
    // Keeps full precision so e.g. 1 ÷ 3 × 3 gives 1, not 0.999999999999999.
    private var displayValue: Double? = null

    // True when a second operand was entered after an operator (by typing, √, x², 1/x, %, +/-, MR...).
    // Used to decide whether pressing another operator must evaluate the pending operation.
    private var hasSecondOperand: Boolean = false

    // Subtext expression displayed on the line above
    private var expression: String = ""

    // Stored first operand for pending binary operations
    private var pendingFirstOperand: Double? = null

    // Pending binary operator (+, -, *, /)
    private var pendingOperator: Operator? = null

    // Formatted representation of the first operand or expression
    private var firstOperandText: String = ""

    // Expression representation of current operand if modified by unary operations (sqr, sqrt, etc.)
    private var currentUnaryExpression: String? = null

    // Last completed binary operator and second operand (used for repeated '=' presses)
    private var lastRepeatedOperator: Operator? = null
    private var lastRepeatedSecondOperand: Double? = null

    // Control flags
    private var isNewEntry: Boolean = true
    private var lastActionWasEquals: Boolean = false
    private var activeError: CalculatorError? = null

    // Memory accumulator (null represents empty memory)
    private var memory: Double? = null

    // History log of completed calculations
    private val historyList = mutableListOf<CalculationHistoryItem>()

    /**
     * Returns an immutable snapshot of current engine state.
     */
    fun getState(): CalculatorState {
        return CalculatorState(
            display = display,
            expression = expression,
            error = activeError,
            memory = memory,
            history = historyList.toList()
        )
    }

    /**
     * Handles digit input (0 through 9).
     */
    fun inputDigit(digit: Int) {
        if (digit !in 0..9) return

        if (activeError != null) {
            clear()
        }

        if (lastActionWasEquals) {
            expression = ""
            lastActionWasEquals = false
            currentUnaryExpression = null
            lastRepeatedOperator = null
            lastRepeatedSecondOperand = null
        }

        displayValue = null
        hasSecondOperand = true
        if (isNewEntry) {
            display = digit.toString()
            isNewEntry = false
            currentUnaryExpression = null
        } else {
            if (display == "0") {
                display = digit.toString()
            } else if (display.length < 16) {
                display += digit.toString()
            }
        }
    }

    /**
     * Handles decimal point input.
     */
    fun inputDecimal() {
        if (activeError != null) {
            clear()
        }

        if (lastActionWasEquals) {
            expression = ""
            lastActionWasEquals = false
            currentUnaryExpression = null
            lastRepeatedOperator = null
            lastRepeatedSecondOperand = null
            display = "0."
            displayValue = null
            isNewEntry = false
            return
        }

        displayValue = null
        hasSecondOperand = true
        if (isNewEntry) {
            display = "0."
            isNewEntry = false
            currentUnaryExpression = null
        } else if (!display.contains('.')) {
            display += "."
        }
    }

    /**
     * Handles backspace (delete a single character from the right).
     */
    fun backspace() {
        if (activeError != null) {
            clear()
            return
        }

        // Backspace is disabled immediately following operator or equals
        if (isNewEntry) return

        displayValue = null
        if (display.length > 1) {
            display = display.dropLast(1)
            if (display == "-" || display.isEmpty()) {
                display = "0"
                isNewEntry = true
            }
        } else {
            display = "0"
            isNewEntry = true
        }
        currentUnaryExpression = null
    }

    /**
     * Toggles the sign of the current number (+/-).
     */
    fun negate() {
        if (activeError != null) return
        if (display == "0" || display == "0.") return

        display = if (display.startsWith("-")) {
            display.substring(1)
        } else {
            "-$display"
        }
        displayValue = displayValue?.let { -it }
        hasSecondOperand = true

        if (currentUnaryExpression != null) {
            currentUnaryExpression = "negate($currentUnaryExpression)"
            if (pendingOperator != null) {
                expression = "$firstOperandText ${pendingOperator!!.symbol} $currentUnaryExpression"
            } else {
                expression = currentUnaryExpression!!
            }
        }
    }

    /**
     * Sets a binary operator (+, −, ×, ÷).
     */
    fun setOperator(op: Operator) {
        if (activeError != null) return

        val currentVal = parseDisplayValue()

        if (pendingOperator != null && hasSecondOperand) {
            // Immediate evaluation of previous operator (Windows standard calculator evaluation)
            val first = pendingFirstOperand ?: 0.0
            val second = currentVal

            if (pendingOperator == Operator.DIVIDE && second == 0.0) {
                activeError = CalculatorError.DIVIDE_BY_ZERO
                expression = "$firstOperandText ${pendingOperator!!.symbol} 0 ="
                display = ""
                return
            }

            val result = executeOperation(first, second, pendingOperator!!)
            if (!result.isFinite()) {
                setOverflow()
                return
            }
            pendingFirstOperand = result
            firstOperandText = formatNumber(result)
            showNumber(result)
        } else {
            pendingFirstOperand = currentVal
            firstOperandText = currentUnaryExpression ?: formatNumber(currentVal)
        }

        pendingOperator = op
        expression = "$firstOperandText ${op.symbol}"
        isNewEntry = true
        hasSecondOperand = false
        lastActionWasEquals = false
        currentUnaryExpression = null
        lastRepeatedOperator = null
        lastRepeatedSecondOperand = null
    }

    /**
     * Executes the calculation when '=' is pressed.
     */
    fun calculateEquals() {
        if (activeError != null) return

        if (pendingOperator != null) {
            val first = pendingFirstOperand ?: 0.0
            val second = parseDisplayValue()
            val op = pendingOperator!!

            if (op == Operator.DIVIDE && second == 0.0) {
                activeError = CalculatorError.DIVIDE_BY_ZERO
                val secondText = currentUnaryExpression ?: formatNumber(second)
                expression = "$firstOperandText ${op.symbol} $secondText ="
                display = ""
                return
            }

            val result = executeOperation(first, second, op)
            if (!result.isFinite()) {
                setOverflow()
                return
            }
            val secondText = currentUnaryExpression ?: formatNumber(second)
            val fullExpression = "$firstOperandText ${op.symbol} $secondText ="
            val formattedResult = formatNumber(result)

            expression = fullExpression
            showNumber(result)

            // Record to history
            historyList.add(0, CalculationHistoryItem(expression = fullExpression, result = formattedResult))

            // Save repeat state
            lastRepeatedOperator = op
            lastRepeatedSecondOperand = second
            pendingOperator = null
            pendingFirstOperand = result
            firstOperandText = formattedResult
            isNewEntry = true
            hasSecondOperand = false
            lastActionWasEquals = true
            currentUnaryExpression = null

        } else if (lastRepeatedOperator != null && lastRepeatedSecondOperand != null) {
            // Repeating '=' action with last operator and operand (Windows feature)
            val first = parseDisplayValue()
            val second = lastRepeatedSecondOperand!!
            val op = lastRepeatedOperator!!

            if (op == Operator.DIVIDE && second == 0.0) {
                activeError = CalculatorError.DIVIDE_BY_ZERO
                expression = "${formatNumber(first)} ${op.symbol} 0 ="
                display = ""
                return
            }

            val result = executeOperation(first, second, op)
            if (!result.isFinite()) {
                setOverflow()
                return
            }
            val fullExpression = "${formatNumber(first)} ${op.symbol} ${formatNumber(second)} ="
            val formattedResult = formatNumber(result)

            expression = fullExpression
            showNumber(result)

            historyList.add(0, CalculationHistoryItem(expression = fullExpression, result = formattedResult))
            pendingFirstOperand = result
            firstOperandText = formattedResult
            isNewEntry = true
            lastActionWasEquals = true
        } else {
            // No operator pending: display current = display
            val currentVal = parseDisplayValue()
            val formatted = formatNumber(currentVal)
            val fullExpression = "$formatted ="
            expression = fullExpression
            showNumber(currentVal)
            historyList.add(0, CalculationHistoryItem(expression = fullExpression, result = formatted))
            isNewEntry = true
            lastActionWasEquals = true
        }
    }

    /**
     * Calculates square root (²√x).
     */
    fun calculateSquareRoot() {
        if (activeError != null) return

        val currentVal = parseDisplayValue()
        if (currentVal < 0.0) {
            activeError = CalculatorError.INVALID_INPUT
            expression = "√( ${formatNumber(currentVal)} )"
            display = ""
            return
        }

        val result = sqrt(currentVal)
        if (!result.isFinite()) {
            setOverflow()
            return
        }
        val prevExpr = currentUnaryExpression ?: formatNumber(currentVal)
        currentUnaryExpression = "√($prevExpr)"

        if (pendingOperator != null) {
            expression = "$firstOperandText ${pendingOperator!!.symbol} $currentUnaryExpression"
        } else {
            expression = currentUnaryExpression!!
        }

        showNumber(result)
        isNewEntry = true
        hasSecondOperand = true
    }

    /**
     * Calculates square (x²).
     */
    fun calculateSquare() {
        if (activeError != null) return

        val currentVal = parseDisplayValue()
        val result = currentVal * currentVal
        if (!result.isFinite()) {
            setOverflow()
            return
        }
        val prevExpr = currentUnaryExpression ?: formatNumber(currentVal)
        currentUnaryExpression = "sqr($prevExpr)"

        if (pendingOperator != null) {
            expression = "$firstOperandText ${pendingOperator!!.symbol} $currentUnaryExpression"
        } else {
            expression = currentUnaryExpression!!
        }

        showNumber(result)
        isNewEntry = true
        hasSecondOperand = true
    }

    /**
     * Calculates fraction (1/x).
     */
    fun calculateFraction() {
        if (activeError != null) return

        val currentVal = parseDisplayValue()
        if (currentVal == 0.0) {
            activeError = CalculatorError.DIVIDE_BY_ZERO
            expression = "1/(0)"
            display = ""
            return
        }

        val result = 1.0 / currentVal
        if (!result.isFinite()) {
            setOverflow()
            return
        }
        val prevExpr = currentUnaryExpression ?: formatNumber(currentVal)
        currentUnaryExpression = "1/($prevExpr)"

        if (pendingOperator != null) {
            expression = "$firstOperandText ${pendingOperator!!.symbol} $currentUnaryExpression"
        } else {
            expression = currentUnaryExpression!!
        }

        showNumber(result)
        isNewEntry = true
        hasSecondOperand = true
    }

    /**
     * Calculates percentage (%) following Windows Calculator Standard behavior:
     * e.g. 50 + 10 % -> 50 + 5.
     */
    fun calculatePercentage() {
        if (activeError != null) return

        val currentVal = parseDisplayValue()

        if (pendingOperator != null) {
            val base = pendingFirstOperand ?: 0.0
            val percentVal = when (pendingOperator) {
                Operator.ADD, Operator.SUBTRACT -> base * (currentVal / 100.0)
                Operator.MULTIPLY, Operator.DIVIDE -> currentVal / 100.0
                null -> 0.0
            }

            if (!percentVal.isFinite()) {
                setOverflow()
                return
            }
            val formattedPercent = formatNumber(percentVal)
            currentUnaryExpression = formattedPercent
            expression = "$firstOperandText ${pendingOperator!!.symbol} $formattedPercent"
            showNumber(percentVal)
            isNewEntry = true
            hasSecondOperand = true
        } else {
            // Standalone percentage resets to 0 in Windows Standard Calculator
            display = "0"
            displayValue = null
            expression = "0"
            isNewEntry = true
            currentUnaryExpression = null
        }
    }

    /**
     * Clear All (C): Resets all arithmetic and display states, keeping history and memory.
     */
    fun clear() {
        display = "0"
        displayValue = null
        hasSecondOperand = false
        expression = ""
        pendingFirstOperand = null
        pendingOperator = null
        firstOperandText = ""
        currentUnaryExpression = null
        lastRepeatedOperator = null
        lastRepeatedSecondOperand = null
        isNewEntry = true
        lastActionWasEquals = false
        activeError = null
    }

    /**
     * Clear Entry (CE): Clears only the current display entry, preserving pending operations.
     */
    fun clearEntry() {
        if (activeError != null) {
            clear()
            return
        }
        display = "0"
        displayValue = null
        hasSecondOperand = pendingOperator != null
        currentUnaryExpression = null
        isNewEntry = true
    }

    // --- Memory Operations ---

    fun memoryClear() {
        memory = null
    }

    fun memoryRecall() {
        if (activeError != null || memory == null) return
        showNumber(memory!!)
        currentUnaryExpression = null
        hasSecondOperand = true
        isNewEntry = true
    }

    fun memoryAdd() {
        if (activeError != null) return
        val current = parseDisplayValue()
        memory = (memory ?: 0.0) + current
        isNewEntry = true
    }

    fun memorySubtract() {
        if (activeError != null) return
        val current = parseDisplayValue()
        memory = (memory ?: 0.0) - current
        isNewEntry = true
    }

    fun memoryStore() {
        if (activeError != null) return
        memory = parseDisplayValue()
        isNewEntry = true
    }

    // --- History Operations ---

    fun loadHistoryItem(item: CalculationHistoryItem) {
        if (activeError != null) {
            clear()
        }
        display = item.result
        displayValue = null
        hasSecondOperand = true
        isNewEntry = true
        lastActionWasEquals = false
        currentUnaryExpression = null
    }

    fun clearHistory() {
        historyList.clear()
    }

    // --- Internal Helpers ---

    private fun parseDisplayValue(): Double {
        return displayValue ?: display.toDoubleOrNull() ?: 0.0
    }

    /** Shows a calculated number, keeping its exact value for the next operation. */
    private fun showNumber(value: Double) {
        display = formatNumber(value)
        displayValue = value
    }

    /** Result is too large (or not a number): show the overflow error instead of a wrong "0". */
    private fun setOverflow() {
        activeError = CalculatorError.OVERFLOW
        display = ""
        displayValue = null
    }

    private fun executeOperation(first: Double, second: Double, op: Operator): Double {
        return when (op) {
            Operator.ADD -> first + second
            Operator.SUBTRACT -> first - second
            Operator.MULTIPLY -> first * second
            Operator.DIVIDE -> first / second
        }
    }

    companion object {
        /**
         * Formats double values cleanly without floating-point artifacts or unwanted trailing zeros.
         */
        fun formatNumber(value: Double): String {
            if (value.isNaN() || value.isInfinite()) return "0"
            if (value == 0.0) return "0"

            // Round to 15 significant digits: removes floating-point noise (0.1 + 0.2 -> 0.3)
            // without turning small numbers into 0 like a fixed number of decimal places would.
            val rounded = BigDecimal(value).round(MathContext(15, RoundingMode.HALF_UP)).stripTrailingZeros()
            val abs = rounded.abs()

            // Very large or very small numbers use scientific notation like Windows (e.g. 1.5e+20)
            if (abs >= LARGE_LIMIT || abs < SMALL_LIMIT) {
                val mantissa = rounded.movePointLeft(rounded.precision() - rounded.scale() - 1)
                val exponent = rounded.precision() - rounded.scale() - 1
                val sign = if (exponent >= 0) "+" else "-"
                return "${mantissa.stripTrailingZeros().toPlainString()}e$sign${kotlin.math.abs(exponent)}"
            }
            return rounded.toPlainString()
        }

        private val LARGE_LIMIT = BigDecimal("1e16")
        private val SMALL_LIMIT = BigDecimal("1e-15")
    }
}
