# Code Review – Win11 Calculator

**Reviewer:** Claude (Anthropic). This review was done in a separate AI session from the code generation, which used Antigravity / Gemini.
**Scope:** `engine/` (CalculatorEngine, CalculatorState, CalculatorError, Operator, CalculationHistoryItem), `viewmodel/CalculatorViewModel.kt`, `ui/` (screen and components), `res/values/strings.xml`, `CalculatorEngineTest.kt`.
**Method:** I read all the code, then compiled and ran the engine with its unit tests. The existing 17 tests passed before any changes. I then reproduced each suspected bug with real inputs.

## What is good

- **Separate engine.** The calculation logic lives in a pure-Kotlin `CalculatorEngine`, with no Android imports and no third-party math library. It only uses `kotlin.math.sqrt` and `java.math.BigDecimal` for formatting.
- **No hard-coded UI text.** All button labels, accessibility descriptions, the history panel and error messages come from `strings.xml`.
- **Windows behaviour.** The engine evaluates left to right (`2 + 3 × 4 = 20`), handles `%` relative to the first operand (`50 + 10 %` → 5), repeats the last operation on repeated `=`, and lets CE clear only the current entry.
- **Rotation.** State is held in a `ViewModel`, so the display and history survive screen rotation.
- **Design.** The layout closely matches the Windows 11 Standard calculator: the expression line, large result, memory row, 6×4 grid and blue `=` button.

## Findings (highest priority first)

| # | Priority | Area | Issue | Reproduced |
|---|----------|------|-------|------------|
| 1 | **High** | Engine – operator chaining | After a unary operation (√, x², 1/x), `%`, `MR` or a history item, pressing another operator drops the pending operation. `setOperator` checks `!isNewEntry`, but these operations set `isNewEntry = true`. | `5 + √9 + =` gave **6** (expected 16). `50 + 10 % × 2 =` gave **10** (expected 110). |
| 2 | **High** | Engine – number precision and range | `formatNumber` returns `"0"` for Infinity, so an overflow shows **0** with no error. Results are rounded to 10 decimal places, so small values become 0. Large values are shown as very long digit strings. The engine re-reads the rounded display text as the next operand, which loses precision. | Squaring 999999999 repeatedly gave **0**. `1 ÷ 100000000000 =` gave **0**. `1 ÷ 3 = × 3 =` gave **0.9999999999**. |
| 3 | Medium | Engine – memory | `M+` and `M−` have no overflow check, so memory can become Infinity without any warning. | — |
| 4 | Medium | Engine / UX | `0 ÷ 0` shows "Cannot divide by zero". Windows shows "Result is undefined". | — |
| 5 | Medium | State | History and the current entry are lost if Android kills the app in the background (process death). `ViewModel` does not use `SavedStateHandle`. | — |
| 6 | Low | UI | The M˅ (memory list) button and the hamburger menu are placeholders and do nothing. | — |
| 7 | Low | UX | Backspace after `=` does nothing. Windows clears the expression line. | — |
| 8 | Low | Engine | The 16-character input limit counts `-` and `.`, so negative or decimal numbers allow fewer digits than Windows. | — |
| 9 | Low | UI | The expression line uses `maxLines = 1` with no scrolling, so long chains like `negate(sqr(√(…)))` are cut off. | — |
| 10 | Low | Code quality | There are many `pendingOperator!!` / `memory!!` force-unwraps. `CalculationHistoryItem.timestamp` is never used, and the history list has no size limit. | — |

## Implemented changes (top 2)

### Fix 1 – Operator chaining after √, x², 1/x, %, +/- and MR

- I added a `hasSecondOperand` flag. Typing a digit or `.`, √, x², 1/x, %, +/-, MR or choosing a history item sets it to `true`. An operator or `=` resets it, and CE keeps it when an operator is pending.
- `setOperator` now evaluates the pending operation when `hasSecondOperand` is true, instead of checking `!isNewEntry`.
- Pressing two operators in a row still just replaces the operator, as in Windows.

### Fix 2 – Number precision, large/small numbers and overflow

- **Formatting.** `formatNumber` now rounds to **15 significant digits** instead of 10 decimal places. `0.1 + 0.2` and `0.7 + 0.1` still show 0.3 and 0.8, and small values like 0.00000000001 are no longer lost.
- **Scientific notation.** Numbers ≥ 1e16 or < 1e-15 are shown like Windows, e.g. `1.5e+20`.
- **Full precision.** A new `displayValue` keeps the exact value behind a calculated result, so the next operation uses full precision. `1 ÷ 3 × 3` now gives **1**.
- **Overflow error.** Results that are infinite or not a number set a new `CalculatorError.OVERFLOW`. The UI shows the localized `error_overflow` text ("Overflow") instead of 0.
- Files changed: `CalculatorEngine.kt`, `CalculatorError.kt`, `CalculatorViewModel.kt` (error mapping) and `strings.xml` (`error_overflow`).

### Tests

I added 11 unit tests to `CalculatorEngineTest.kt`:

- **Chaining after unary operations:** chaining after √, after %, after x² and 1/x, MR used as the second operand, and replacing an operator without evaluating it.
- **Formatting and precision:** 0.1 + 0.2, keeping full precision, small numbers, scientific notation, overflow error and `formatNumber`.

| Input | Before | After |
|-------|--------|-------|
| `5 + √9 + =` | 6 | 16 |
| `50 + 10 % × 2 =` | 10 | 110 |
| `1 ÷ 3 = × 3 =` | 0.9999999999 | 1 |
| `1 ÷ 100000000000 =` | 0 | 0.00000000001 |
| 999999999 x² (×10) | 0 | Overflow (error) |

**Result:** all 28 unit tests pass (the 17 existing ones and 11 new ones).
