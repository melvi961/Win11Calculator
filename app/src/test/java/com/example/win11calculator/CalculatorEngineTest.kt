package com.example.win11calculator

import com.example.win11calculator.engine.CalculatorEngine
import com.example.win11calculator.engine.CalculatorError
import com.example.win11calculator.engine.Operator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setUp() {
        engine = CalculatorEngine()
    }

    @Test
    fun testInitialState() {
        val state = engine.getState()
        assertEquals("0", state.display)
        assertEquals("", state.expression)
        assertNull(state.error)
        assertNull(state.memory)
        assertEquals(0, state.history.size)
    }

    @Test
    fun testSimpleAddition() {
        engine.inputDigit(1)
        engine.inputDigit(2)
        assertEquals("12", engine.getState().display)

        engine.setOperator(Operator.ADD)
        assertEquals("12 +", engine.getState().expression)

        engine.inputDigit(8)
        assertEquals("8", engine.getState().display)

        engine.calculateEquals()
        val state = engine.getState()
        assertEquals("20", state.display)
        assertEquals("12 + 8 =", state.expression)
        assertEquals(1, state.history.size)
        assertEquals("12 + 8 =", state.history[0].expression)
        assertEquals("20", state.history[0].result)
    }

    @Test
    fun testChainedOperationsLeftToRight() {
        // In Windows Standard mode: 2 + 3 * 4 = 20 (immediate execution, not precedence)
        engine.inputDigit(2)
        engine.setOperator(Operator.ADD)
        engine.inputDigit(3)
        engine.setOperator(Operator.MULTIPLY) // Executes 2 + 3 = 5

        assertEquals("5", engine.getState().display)
        assertEquals("5 ×", engine.getState().expression)

        engine.inputDigit(4)
        engine.calculateEquals()

        assertEquals("20", engine.getState().display)
        assertEquals("5 × 4 =", engine.getState().expression)
    }

    @Test
    fun testDivisionByZero() {
        engine.inputDigit(9)
        engine.setOperator(Operator.DIVIDE)
        engine.inputDigit(0)
        engine.calculateEquals()

        val state = engine.getState()
        assertEquals(CalculatorError.DIVIDE_BY_ZERO, state.error)
    }

    @Test
    fun testSquareRoot() {
        engine.inputDigit(9)
        engine.calculateSquareRoot()
        assertEquals("3", engine.getState().display)
        assertEquals("√(9)", engine.getState().expression)
    }

    @Test
    fun testSquareRootOfNegativeNumber() {
        engine.inputDigit(4)
        engine.negate()
        assertEquals("-4", engine.getState().display)

        engine.calculateSquareRoot()
        assertEquals(CalculatorError.INVALID_INPUT, engine.getState().error)
    }

    @Test
    fun testSquare() {
        engine.inputDigit(5)
        engine.calculateSquare()
        assertEquals("25", engine.getState().display)
        assertEquals("sqr(5)", engine.getState().expression)
    }

    @Test
    fun testFraction() {
        engine.inputDigit(8)
        engine.calculateFraction()
        assertEquals("0.125", engine.getState().display)
        assertEquals("1/(8)", engine.getState().expression)
    }

    @Test
    fun testFractionZero() {
        engine.inputDigit(0)
        engine.calculateFraction()
        assertEquals(CalculatorError.DIVIDE_BY_ZERO, engine.getState().error)
    }

    @Test
    fun testPercentageLikeWindows() {
        // Windows behavior: 50 + 10 % -> 50 + 5, then = -> 55
        engine.inputDigit(5)
        engine.inputDigit(0)
        engine.setOperator(Operator.ADD)
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.calculatePercentage()

        assertEquals("5", engine.getState().display)
        assertEquals("50 + 5", engine.getState().expression)

        engine.calculateEquals()
        assertEquals("55", engine.getState().display)
        assertEquals("50 + 5 =", engine.getState().expression)
    }

    @Test
    fun testBackspace() {
        engine.inputDigit(1)
        engine.inputDigit(2)
        engine.inputDigit(3)
        assertEquals("123", engine.getState().display)

        engine.backspace()
        assertEquals("12", engine.getState().display)

        engine.backspace()
        assertEquals("1", engine.getState().display)

        engine.backspace()
        assertEquals("0", engine.getState().display)
    }

    @Test
    fun testNegate() {
        engine.inputDigit(7)
        engine.negate()
        assertEquals("-7", engine.getState().display)

        engine.negate()
        assertEquals("7", engine.getState().display)
    }

    @Test
    fun testClearEntryPreservesOperator() {
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.setOperator(Operator.ADD)
        engine.inputDigit(9)
        assertEquals("9", engine.getState().display)

        engine.clearEntry()
        assertEquals("0", engine.getState().display)
        assertEquals("10 +", engine.getState().expression)

        engine.inputDigit(5)
        engine.calculateEquals()
        assertEquals("15", engine.getState().display)
    }

    @Test
    fun testClearAll() {
        engine.inputDigit(1)
        engine.setOperator(Operator.ADD)
        engine.inputDigit(2)
        engine.clear()

        val state = engine.getState()
        assertEquals("0", state.display)
        assertEquals("", state.expression)
        assertNull(state.error)
    }

    @Test
    fun testMemoryOperations() {
        // Store 42
        engine.inputDigit(4)
        engine.inputDigit(2)
        engine.memoryStore()
        assertEquals(42.0, engine.getState().memory)

        // Clear display, do something else
        engine.clear()
        engine.inputDigit(1)
        engine.inputDigit(0)

        // Add 10 to memory -> memory becomes 52
        engine.memoryAdd()
        assertEquals(52.0, engine.getState().memory)

        // Subtract 2 from memory -> memory becomes 50
        engine.clear()
        engine.inputDigit(2)
        engine.memorySubtract()
        assertEquals(50.0, engine.getState().memory)

        // Recall memory
        engine.clear()
        engine.memoryRecall()
        assertEquals("50", engine.getState().display)

        // Clear memory
        engine.memoryClear()
        assertNull(engine.getState().memory)
    }

    @Test
    fun testRepeatEquals() {
        // 10 + 2 = 12, then hitting = again repeats + 2 -> 14
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.setOperator(Operator.ADD)
        engine.inputDigit(2)
        engine.calculateEquals()
        assertEquals("12", engine.getState().display)

        engine.calculateEquals()
        assertEquals("14", engine.getState().display)

        engine.calculateEquals()
        assertEquals("16", engine.getState().display)
    }

    @Test
    fun testHistorySelectionAndClear() {
        engine.inputDigit(4)
        engine.setOperator(Operator.MULTIPLY)
        engine.inputDigit(5)
        engine.calculateEquals()
        assertEquals("20", engine.getState().display)
        assertEquals(1, engine.getState().history.size)

        // Input something new
        engine.inputDigit(9)
        assertEquals("9", engine.getState().display)

        // Load item back from history
        val item = engine.getState().history[0]
        engine.loadHistoryItem(item)
        assertEquals("20", engine.getState().display)

        // Clear history
        engine.clearHistory()
        assertEquals(0, engine.getState().history.size)
    }

    // --- Code review fix 1: operator chaining after unary operations / % / MR ---

    @Test
    fun testOperatorAfterSquareRootEvaluatesPendingOperation() {
        // 5 + √9 + -> 8 (previously the 5 was lost and the result was 3)
        engine.inputDigit(5)
        engine.setOperator(Operator.ADD)
        engine.inputDigit(9)
        engine.calculateSquareRoot()
        engine.setOperator(Operator.ADD)
        assertEquals("8", engine.getState().display)
        assertEquals("8 +", engine.getState().expression)

        engine.inputDigit(2)
        engine.calculateEquals()
        assertEquals("10", engine.getState().display)
    }

    @Test
    fun testOperatorAfterPercentEvaluatesPendingOperation() {
        // 50 + 10 % × 2 = -> (50 + 5) × 2 = 110 (previously 10)
        engine.inputDigit(5); engine.inputDigit(0)
        engine.setOperator(Operator.ADD)
        engine.inputDigit(1); engine.inputDigit(0)
        engine.calculatePercentage()
        engine.setOperator(Operator.MULTIPLY)
        assertEquals("55", engine.getState().display)
        engine.inputDigit(2)
        engine.calculateEquals()
        assertEquals("110", engine.getState().display)
    }

    @Test
    fun testOperatorAfterSquareAndFractionEvaluatesPendingOperation() {
        // 2 × 3 x² − -> 18 ; then 4 1/x = -> 18 − 0.25 = 17.75
        engine.inputDigit(2)
        engine.setOperator(Operator.MULTIPLY)
        engine.inputDigit(3)
        engine.calculateSquare()
        engine.setOperator(Operator.SUBTRACT)
        assertEquals("18", engine.getState().display)
        engine.inputDigit(4)
        engine.calculateFraction()
        engine.calculateEquals()
        assertEquals("17.75", engine.getState().display)
    }

    @Test
    fun testMemoryRecallAsSecondOperandIsUsed() {
        engine.inputDigit(7)
        engine.memoryStore()
        engine.clear()
        engine.inputDigit(3)
        engine.setOperator(Operator.ADD)
        engine.memoryRecall()
        engine.setOperator(Operator.ADD)
        assertEquals("10", engine.getState().display)
    }

    @Test
    fun testChangingOperatorDoesNotEvaluate() {
        // 5 + then × replaces the operator without calculating
        engine.inputDigit(5)
        engine.setOperator(Operator.ADD)
        engine.setOperator(Operator.MULTIPLY)
        assertEquals("5 ×", engine.getState().expression)
        engine.inputDigit(3)
        engine.calculateEquals()
        assertEquals("15", engine.getState().display)
    }

    // --- Code review fix 2: number precision, large/small numbers and overflow ---

    @Test
    fun testFloatingPointNoiseIsHidden() {
        // 0.1 + 0.2 = 0.3
        engine.inputDecimal(); engine.inputDigit(1)
        engine.setOperator(Operator.ADD)
        engine.inputDecimal(); engine.inputDigit(2)
        engine.calculateEquals()
        assertEquals("0.3", engine.getState().display)
    }

    @Test
    fun testFullPrecisionIsKeptBetweenOperations() {
        // 1 ÷ 3 = × 3 = -> 1 (previously 0.9999999999)
        engine.inputDigit(1)
        engine.setOperator(Operator.DIVIDE)
        engine.inputDigit(3)
        engine.calculateEquals()
        engine.setOperator(Operator.MULTIPLY)
        engine.inputDigit(3)
        engine.calculateEquals()
        assertEquals("1", engine.getState().display)
    }

    @Test
    fun testSmallNumbersAreNotRoundedToZero() {
        // 8 1/x = 0.125 and 1 ÷ 100000000000 = 0.00000000001
        engine.inputDigit(8)
        engine.calculateFraction()
        assertEquals("0.125", engine.getState().display)

        engine.clear()
        engine.inputDigit(1)
        engine.setOperator(Operator.DIVIDE)
        engine.inputDigit(1)
        repeat(11) { engine.inputDigit(0) }
        engine.calculateEquals()
        assertEquals("0.00000000001", engine.getState().display)
    }

    @Test
    fun testLargeNumbersUseScientificNotation() {
        // 99999999 x² x² -> 9.9999996e+31 instead of a 32-digit string
        // (results are shown with 15 significant digits)
        repeat(8) { engine.inputDigit(9) }
        engine.calculateSquare()
        assertEquals("9999999800000000", engine.getState().display)
        engine.calculateSquare()
        assertEquals("9.9999996e+31", engine.getState().display)
        assertNull(engine.getState().error)
    }

    @Test
    fun testOverflowShowsErrorInsteadOfZero() {
        // Squaring a big number repeatedly eventually exceeds Double range
        repeat(9) { engine.inputDigit(9) }
        repeat(10) { engine.calculateSquare() }
        assertEquals(CalculatorError.OVERFLOW, engine.getState().error)
        assertEquals("", engine.getState().display)

        // C recovers from the error
        engine.clear()
        assertNull(engine.getState().error)
        assertEquals("0", engine.getState().display)
    }

    @Test
    fun testFormatNumber() {
        assertEquals("0", CalculatorEngine.formatNumber(0.0))
        assertEquals("20", CalculatorEngine.formatNumber(20.0))
        assertEquals("-2.5", CalculatorEngine.formatNumber(-2.5))
        assertEquals("0.333333333333333", CalculatorEngine.formatNumber(1.0 / 3.0))
        assertEquals("1.5e+20", CalculatorEngine.formatNumber(1.5e20))
        assertEquals("1.25e-20", CalculatorEngine.formatNumber(1.25e-20))
        assertEquals("0.8", CalculatorEngine.formatNumber(0.7 + 0.1))
    }
}
