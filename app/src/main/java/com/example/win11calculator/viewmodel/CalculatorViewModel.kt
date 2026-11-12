package com.example.win11calculator.viewmodel

import androidx.lifecycle.ViewModel
import com.example.win11calculator.R
import com.example.win11calculator.engine.CalculationHistoryItem
import com.example.win11calculator.engine.CalculatorEngine
import com.example.win11calculator.engine.CalculatorError
import com.example.win11calculator.engine.Operator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state consumed by the calculator screen.
 */
data class CalculatorUiState(
    val display: String = "0",
    val expression: String = "",
    val isError: Boolean = false,
    val errorStringRes: Int? = null,
    val memory: Double? = null,
    val isMemoryActive: Boolean = false,
    val history: List<CalculationHistoryItem> = emptyList(),
    val isHistoryOpen: Boolean = false
)

/**
 * ViewModel managing the state and interactions of the Windows 11 Calculator.
 */
class CalculatorViewModel(
    private val engine: CalculatorEngine = CalculatorEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var isHistoryOpen: Boolean = false

    private fun syncState() {
        _uiState.update { buildUiState() }
    }

    private fun buildUiState(): CalculatorUiState {
        val engineState = engine.getState()
        val errorRes = when (engineState.error) {
            CalculatorError.DIVIDE_BY_ZERO -> R.string.error_divide_by_zero
            CalculatorError.INVALID_INPUT -> R.string.error_invalid_input
            CalculatorError.OVERFLOW -> R.string.error_overflow
            null -> null
        }

        return CalculatorUiState(
            display = engineState.display,
            expression = engineState.expression,
            isError = engineState.error != null,
            errorStringRes = errorRes,
            memory = engineState.memory,
            isMemoryActive = engineState.memory != null,
            history = engineState.history,
            isHistoryOpen = isHistoryOpen
        )
    }

    // --- Inputs ---

    fun onDigit(digit: Int) {
        engine.inputDigit(digit)
        syncState()
    }

    fun onDecimal() {
        engine.inputDecimal()
        syncState()
    }

    fun onBackspace() {
        engine.backspace()
        syncState()
    }

    fun onNegate() {
        engine.negate()
        syncState()
    }

    fun onOperator(op: Operator) {
        engine.setOperator(op)
        syncState()
    }

    fun onEquals() {
        engine.calculateEquals()
        syncState()
    }

    fun onSquareRoot() {
        engine.calculateSquareRoot()
        syncState()
    }

    fun onSquare() {
        engine.calculateSquare()
        syncState()
    }

    fun onFraction() {
        engine.calculateFraction()
        syncState()
    }

    fun onPercent() {
        engine.calculatePercentage()
        syncState()
    }

    fun onClear() {
        engine.clear()
        syncState()
    }

    fun onClearEntry() {
        engine.clearEntry()
        syncState()
    }

    // --- Memory Actions ---

    fun onMemoryClear() {
        engine.memoryClear()
        syncState()
    }

    fun onMemoryRecall() {
        engine.memoryRecall()
        syncState()
    }

    fun onMemoryAdd() {
        engine.memoryAdd()
        syncState()
    }

    fun onMemorySubtract() {
        engine.memorySubtract()
        syncState()
    }

    fun onMemoryStore() {
        engine.memoryStore()
        syncState()
    }

    // --- History Panel ---

    fun toggleHistory() {
        isHistoryOpen = !isHistoryOpen
        syncState()
    }

    fun setHistoryOpen(open: Boolean) {
        isHistoryOpen = open
        syncState()
    }

    fun onSelectHistoryItem(item: CalculationHistoryItem) {
        engine.loadHistoryItem(item)
        isHistoryOpen = false
        syncState()
    }

    fun onClearHistory() {
        engine.clearHistory()
        syncState()
    }
}
