# Windows 11 Standard Calculator for Android

A faithful, modern Android port of the **Windows 11 Standard Calculator**, built natively using **Kotlin** and **Jetpack Compose** with **Material 3**.

The application replicates the exact evaluation logic, user experience, visual styling, memory operations, and error handling of the Windows 11 Standard Calculator—including immediate left-to-right arithmetic execution, chained unary transforms, repeat equals, contextual percentage evaluation, and fluent light/dark themes.

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
  - [Arithmetic & Unary Operations](#arithmetic--unary-operations)
  - [Editing & Entry Controls](#editing--entry-controls)
  - [Memory System](#memory-system)
  - [Calculation History Panel](#calculation-history-panel)
  - [Windows 11 Fluent UI & Styling](#windows-11-fluent-ui--styling)
- [Operation Behavior & Calculation Logic](#operation-behavior--calculation-logic)
  - [Left-to-Right Evaluation vs. Precedence](#left-to-right-evaluation-vs-precedence)
  - [Operator Chaining & Replacement](#operator-chaining--replacement)
  - [Square Root (²√x)](#square-root-x)
  - [Square (x²)](#square-x)
  - [Reciprocal / Fraction (1/x)](#reciprocal--fraction-1x)
  - [Contextual Percentage (%)](#contextual-percentage-)
  - [Chaining Unary Operations with Binary Operators](#chaining-unary-operations-with-binary-operators)
  - [Repeat Equals (=) Action](#repeat-equals--action)
  - [High Precision & Scientific Notation](#high-precision--scientific-notation)
- [Error Messages & Handling](#error-messages--handling)
- [Step-by-Step Usage Guide](#step-by-step-usage-guide)
- [Project Architecture & Structure](#project-architecture--structure)
  - [Directory Tree](#directory-tree)
  - [The Pure Kotlin Calculator Engine](#the-pure-kotlin-calculator-engine)
- [Building & Running in Android Studio](#building--running-in-android-studio)
- [Running Unit Tests](#running-unit-tests)
- [How to Add a New Language (Localization)](#how-to-add-a-new-language-localization)
- [GitHub Repository Description](#github-repository-description)

---

## Overview

Unlike standard mobile calculators that force algebraic operator precedence (order of operations, PEMDAS), standard desktop calculators—specifically Windows 11 in Standard mode—evaluate calculations immediately from left to right as each operator is entered. 

This Android project faithfully implements that exact standard calculator behavior from scratch:
- **Zero Third-Party Math Libraries:** Every calculation, string representation, operator stack, and formatting rule is written in pure Kotlin.
- **Clean Architecture (MVVM):** Strict separation between computation (`engine`), state observation (`viewmodel`), and presentation (`ui`).
- **Full Localization Support:** Zero hard-coded user-facing strings; all button labels, accessibility content descriptions, and error strings reside in `res/values/strings.xml`.

---

## Key Features

### Arithmetic & Unary Operations

| Symbol | Operation | Type | Description |
| :---: | :--- | :--- | :--- |
| `+` | Addition | Binary | Adds the first operand to the second operand. |
| `−` | Subtraction | Binary | Subtracts the second operand from the first operand. |
| `×` | Multiplication | Binary | Multiplies the two operands. |
| `÷` | Division | Binary | Divides the first operand by the second operand (guards against 0). |
| `²√x` | Square Root | Unary | Computes the principal square root $\sqrt{x}$ (requires $x \ge 0$). |
| `x²` | Square | Unary | Computes $x^2$. |
| `1/x` | Reciprocal | Unary | Computes the multiplicative inverse $\frac{1}{x}$ (guards against $x = 0$). |
| `+/-` | Negate | Unary | Toggles the sign between positive and negative. |
| `%` | Percentage | Contextual | Computes percentage relative to the pending operator and base number. |
| `=` | Equals | Execution | Finalizes the calculation and registers it to calculation history; pressing repeatedly repeats the last operation. |

### Editing & Entry Controls

- **Backspace (`⌫`)**: Deletes the rightmost digit from the current input. If the display is reduced to an empty string or just `"-"`, it resets to `"0"`. Disabled immediately following an operator or `=` press.
- **Clear Entry (`CE`)**: Resets the current display number back to `"0"` without disturbing any pending binary operator, stored first operand, or expression history.
- **Clear (`C`)**: Completely resets the active calculation, cleared operator state, and expression line back to `"0"`. Preserves the Memory register and Calculation History.
- **Sign Toggle (`+/-`)**: Inverts the sign of the active display entry. If performed after unary operations, wraps the active sub-expression in `negate(...)`.
- **Decimal Point (`.`)**: Appends a fractional dot, ensuring only one decimal point can exist per number. When clicked on a fresh entry, automatically formats as `"0."`.

### Memory System

The memory row provides 6 dedicated buttons situated above the numeric keypad:

```
[ MC ]   [ MR ]   [ M+ ]   [ M− ]   [ MS ]   [ M˅ ]
```

- **`MC` (Memory Clear)**: Erases the memory accumulator (`memory = null`). Disabled when memory is empty.
- **`MR` (Memory Recall)**: Recalls the stored memory value into the current entry display and sets the state so it can immediately act as the second operand for pending operations. Disabled when memory is empty.
- **`M+` (Memory Add)**: Adds the active display number to the current memory value (initializes memory from `0.0` if empty).
- **`M−` (Memory Subtract)**: Subtracts the active display number from the current memory value.
- **`MS` (Memory Store)**: Overwrites the memory register with the active number on the display.
- **`M˅` (Memory View)**: Visual indicator button for stored memory (disabled when memory is empty).
- **State-Aware Styling**: Memory buttons dynamically dim when disabled and highlight when active according to the Windows 11 design spec.

### Calculation History Panel

- **Sliding Modal Bottom Sheet**: Accessible anytime by tapping the **Clock / History icon** in the top bar.
- **Chronological Audit**: Displays all completed calculations (newest first), detailing both the full expression (e.g., `12 + 8 =`) and the computed result (`20`).
- **Interactive Restoration**: Tapping any historical record closes the sheet and loads that result into the active display for immediate chaining.
- **Trash Can Action**: Tap the delete icon in the sheet header to clear all saved history.
- **Empty State**: Displays `"There's no history yet"` when no calculations have been executed.
- **Orientation Resilience**: History state is managed inside `CalculatorViewModel`, preserving entries through Android configuration and orientation changes.

### Windows 11 Fluent UI & Styling

- **Two-Line Dynamic Display**:
  - **Upper line**: Shows the pending expression chain in light gray text (e.g., `5 + √(9)` or `50 + 5 =`).
  - **Lower readout**: Shows the active entry or final result. Automatically scales down in size as numbers grow (`46sp` $\rightarrow$ `36sp` $\rightarrow$ `28sp`) and supports horizontal scrolling so digits are never clipped.
- **Visual Button Hierarchy**:
  - **Digit buttons (`0`–`9`, `.`, `+/-`)**: High-contrast pure white surface in Light Mode, dark surface in Dark Mode, semi-bold font weight.
  - **Operator & Function buttons (`%`, `CE`, `C`, `⌫`, `1/x`, `x²`, `²√x`, `÷`, `×`, `−`, `+`)**: Soft neutral gray buttons for clear visual separation.
  - **Equals button (`=`)**: Signature Windows 11 Accent Blue (`#0067C0` in Light Mode, `#4CC2FF` in Dark Mode) with bold high-contrast text.
- **Adaptive System Themes**: Seamlessly switches between Windows 11 Light and Dark themes based on the Android device's system settings.
- **Accessibility Ready**: Every button, icon, and display element includes full accessibility content descriptions configured in `strings.xml`.

---

## Operation Behavior & Calculation Logic

### Left-to-Right Evaluation vs. Precedence

Windows 11 Standard Calculator evaluates calculations **sequentially from left to right** rather than enforcing algebraic operator precedence.

#### Example: `2 + 3 × 4 =`
1. Enter `2`
2. Press `+` (expression: `2 +`)
3. Enter `3`
4. Press `×` $\rightarrow$ Windows evaluates `2 + 3` immediately to `5` (expression: `5 ×`, display: `5`)
5. Enter `4`
6. Press `=` $\rightarrow$ Computes `5 × 4 = 20` (expression: `5 × 4 =`, display: `20`)

*(Note: In a scientific calculator with algebraic precedence, $2 + 3 \times 4$ would equal 14; in Windows Standard Mode it equals 20).*

---

### Operator Chaining & Replacement

- **Replacing an Operator**: If you press `5` followed by `+`, but change your mind and press `×`, the calculator updates the pending operator to `×` without executing:
  - Expression updates from `5 +` to `5 ×`.
- **Immediate Evaluation on Chaining**: When a second operand is entered, pressing another binary operator executes the pending operation first, displays the subtotal, and queues the new operator:
  - Input: `10 + 5 − 3 =`
  - Pressing `−` after entering `5` immediately computes `10 + 5 = 15`. Expression displays `15 −`.
  - Entering `3` and pressing `=` computes `15 − 3 = 12`.

---

### Square Root (²√x)

Calculates the principal square root $\sqrt{x}$.

- **Standalone Example**:
  - Input: `9` $\rightarrow$ Press `²√x`
  - Readout displays: `3`
  - Expression line shows: `√(9)`
- **Nested Unary Example**:
  - Input: `16` $\rightarrow$ Press `²√x` (displays `4`, expr `√(16)`) $\rightarrow$ Press `²√x` (displays `2`, expr `√(√(16))`)
- **Negative Value (Error)**:
  - Input: `-4` $\rightarrow$ Press `²√x`
  - Primary display shows: `Invalid input`
  - Expression line shows: `√( -4 )`

---

### Square (x²)

Multiplies the current number by itself.

- **Standalone Example**:
  - Input: `5` $\rightarrow$ Press `x²`
  - Readout displays: `25`
  - Expression line shows: `sqr(5)`
- **Chained Example**:
  - Input: `2 × 3` $\rightarrow$ Press `x²`
  - Readout displays: `9`
  - Expression line shows: `2 × sqr(3)`
  - Press `=`: Computes `2 × 9 = 18` (expression: `2 × sqr(3) =`)

---

### Reciprocal / Fraction (1/x)

Calculates the multiplicative inverse $\frac{1}{x}$.

- **Standalone Example**:
  - Input: `8` $\rightarrow$ Press `1/x`
  - Readout displays: `0.125`
  - Expression line shows: `1/(8)`
- **Zero Input (Error)**:
  - Input: `0` $\rightarrow$ Press `1/x`
  - Primary display shows: `Cannot divide by zero`
  - Expression line shows: `1/(0)`

---

### Contextual Percentage (%)

The `%` operator adapts its behavior based on whether a binary operation is pending:

#### 1. Additive / Subtractive Percentage: `base ± (base × rate / 100)`
When used with `+` or `−`, the percentage calculates the relative fraction of the first operand:
- **Example (`50 + 10 %`)**:
  1. Input `50`
  2. Press `+` (expression: `50 +`)
  3. Input `10`
  4. Press `%` $\rightarrow$ Evaluates $50 \times \frac{10}{100} = 5$. Display shows `5`, expression updates to `50 + 5`.
  5. Press `=` $\rightarrow$ Evaluates $50 + 5 = 55$. Expression: `50 + 5 =`, display: `55`.
- **Example (`100 − 20 %`)**:
  - Display converts to `20`. Expression becomes `100 − 20`. Pressing `=` results in `80`.

#### 2. Multiplicative / Divisive Percentage: `rate / 100`
When used with `×` or `÷`, `%` simply divides the current operand by 100:
- **Example (`200 × 15 %`)**:
  - Pressing `%` converts `15` to `0.15`. Expression displays `200 × 0.15`. Pressing `=` results in `30`.

#### 3. Standalone Percentage
- Pressing `%` with no pending operator resets the display and expression to `0`, exactly like Windows 11 Standard Calculator.

---

### Chaining Unary Operations with Binary Operators

The engine supports full chaining where unary operations (`√`, `x²`, `1/x`, `+/-`), `%`, or memory recalls serve as the second operand for subsequent binary operations:

#### Example: `5 + √9 + =`
1. Input `5`
2. Press `+` (expression: `5 +`)
3. Input `9`
4. Press `²√x` (display: `3`, expression: `5 + √(9)`)
5. Press `+` $\rightarrow$ Recognizes `3` as the evaluated second operand, executes $5 + 3 = 8$, displays `8`, and sets expression to `8 +`.
6. Press `=` $\rightarrow$ Completes the repeated operation ($8 + 8$), displaying `16`.

#### Example: `50 + 10 % × 2 =`
1. Input `50`
2. Press `+`
3. Input `10`
4. Press `%` (display: `5`, expression: `50 + 5`)
5. Press `×` $\rightarrow$ Evaluates $50 + 5 = 55$, displays `55`, and sets expression to `55 ×`.
6. Input `2`
7. Press `=` $\rightarrow$ Evaluates $55 \times 2 = 110$.

#### Example: `18 − 4 1/x =`
1. Input `18` $\rightarrow$ Press `−` $\rightarrow$ Input `4`
2. Press `1/x` (display: `0.25`, expression: `18 − 1/(4)`)
3. Press `=` $\rightarrow$ Displays `17.75`.

---

### Repeat Equals (=) Action

Pressing `=` repeatedly continues to re-apply the last operator and second operand to the current result:

- **Example with Addition (`10 + 2 =`)**:
  - First press of `=`: $10 + 2 =$ **`12`**
  - Second press of `=`: $12 + 2 =$ **`14`**
  - Third press of `=`: $14 + 2 =$ **`16`**
- **Example with Division (`100 ÷ 2 =`)**:
  - First press of `=`: $100 \div 2 =$ **`50`**
  - Second press of `=`: $50 \div 2 =$ **`25`**
  - Third press of `=`: $25 \div 2 =$ **`12.5`**

---

### High Precision & Scientific Notation

1. **Floating-Point Artifact Removal**: Values are formatted with 15 significant digits (`MathContext(15, RoundingMode.HALF_UP)`), eliminating IEEE 754 precision noise. For instance, `0.1 + 0.2` displays clean `0.3`, and `0.7 + 0.1` displays `0.8`.
2. **Exact Internal Value (`displayValue`)**: Calculated results retain their exact 64-bit IEEE 754 `Double` value behind the scenes instead of parsing back the rounded display string:
   - `1 ÷ 3 =` displays `0.333333333333333`.
   - Then pressing `× 3 =` results in **`1`** (rather than `0.9999999999`).
3. **Scientific Notation**: Numbers with magnitude $\ge 10^{16}$ or $< 10^{-15}$ automatically format in clean scientific notation (e.g., `1.5e+20` or `1.25e-20`), preventing display clipping.

---

## Error Messages & Handling

All error states are enumerated in `CalculatorError`, mapped in `CalculatorViewModel`, and localized in `res/values/strings.xml`:

| Error Type | Trigger Conditions | Expression Display | Main Readout | Recovery Action |
| :--- | :--- | :--- | :--- | :--- |
| **`DIVIDE_BY_ZERO`** | Attempting to divide by zero (`n ÷ 0 =`), or taking reciprocal of zero (`0` followed by `1/x`). | `n ÷ 0 =` or `1/(0)` | `Cannot divide by zero` | Press `C`, `CE`, or type any digit. |
| **`INVALID_INPUT`** | Attempting to compute the square root of a negative number (e.g., `-9` followed by `²√x`). | `√( -9 )` | `Invalid input` | Press `C`, `CE`, or type any digit. |
| **`OVERFLOW`** | Result exceeds the maximum finite representation of a 64-bit float ($\pm 1.79769 \times 10^{308}$) resulting in $\pm\infty$ or `NaN` (e.g., squaring `999999999` repeatedly). | *(cleared)* | `Overflow` | Press `C` to reset. |

---

## Step-by-Step Usage Guide

### 1. Basic Arithmetic
1. Tap `1`, `2` to enter `12`.
2. Tap `+`. Notice `12 +` appears on the upper expression line.
3. Tap `8`.
4. Tap `=`. The main display shows `20`, and `12 + 8 =` moves to calculation history.

### 2. Using the Memory Register
1. Type `25` and tap `MS`. Memory is saved (`25`), and `MC`, `MR`, `M˅` become active.
2. Tap `C` to clear the display.
3. Type `5` and tap `M+`. The memory value updates to `30`.
4. Tap `C` again. Tap `MR`. The display restores `30`.
5. Tap `MC`. The memory is cleared, and `MC`, `MR`, `M˅` dim to their disabled state.

### 3. Correcting Mistakes
- **Typed wrong digit**: Tap `⌫` (backspace) to erase the last digit.
- **Typed wrong second operand**: Tap `CE` (clear entry) to clear the active number back to `0` while keeping the pending operator and first operand intact.
- **Want to restart**: Tap `C` to clear everything back to initial state.

### 4. Viewing & Reusing History
1. Perform one or more calculations (e.g., `4 × 5 = 20`).
2. Tap the **History (clock) icon** in the top bar to slide open the history panel.
3. Tap the entry `4 × 5 = 20`. The sheet closes and `20` is loaded into the display ready for your next operation.
4. To delete history, reopen the sheet and tap the **Trash icon** in the top right.

---

## Project Architecture & Structure

The codebase adheres strictly to **Clean Architecture** principles and Android's official **MVVM** pattern:

```
┌───────────────────────────────────────────────┐
│              UI Layer (Compose)               │
│  CalculatorScreen, CalculatorButtonGrid, etc. │
└───────────────────────┬───────────────────────┘
                        │ observes StateFlow<CalculatorUiState>
                        ▼
┌───────────────────────────────────────────────┐
│                 ViewModel                     │
│            CalculatorViewModel                │
└───────────────────────┬───────────────────────┘
                        │ delegates business logic
                        ▼
┌───────────────────────────────────────────────┐
│        Domain Engine (Pure Kotlin)            │
│  CalculatorEngine, CalculatorState, etc.      │
│     * ZERO Android dependencies               │
│     * ZERO 3rd-party math libraries           │
└───────────────────────────────────────────────┘
```

### Directory Tree

```
win11-calculator/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/win11calculator/
│   │   │   │   ├── MainActivity.kt                    # Single Activity entry point
│   │   │   │   ├── engine/                            # Pure Kotlin Engine module
│   │   │   │   │   ├── CalculationHistoryItem.kt      # Immutable history model
│   │   │   │   │   ├── CalculatorEngine.kt            # Core arithmetic & state engine
│   │   │   │   │   ├── CalculatorError.kt             # Domain error enum
│   │   │   │   │   ├── CalculatorState.kt             # Immutable engine snapshot model
│   │   │   │   │   └── Operator.kt                    # Operator enum (+, −, ×, ÷)
│   │   │   │   ├── ui/                                # Jetpack Compose UI
│   │   │   │   │   ├── CalculatorScreen.kt            # Root Scaffold layout
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── CalculatorButtonGrid.kt    # 4×6 responsive button grid
│   │   │   │   │   │   ├── CalculatorDisplay.kt       # Dual-line auto-scaling readout
│   │   │   │   │   │   ├── CalculatorTopBar.kt        # Menu title & history toggle
│   │   │   │   │   │   ├── HistoryBottomSheet.kt      # Slide-up history sheet
│   │   │   │   │   │   └── MemoryRow.kt               # MC, MR, M+, M-, MS, M˅ row
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt                   # Fluent Light & Dark palettes
│   │   │   │   │       ├── Theme.kt                   # Win11CalculatorTheme provider
│   │   │   │   │       └── Type.kt                    # Typography definitions
│   │   │   │   └── viewmodel/
│   │   │   │       └── CalculatorViewModel.kt         # MVVM StateFlow coordinator
│   │   │   └── res/
│   │   │       └── values/
│   │   │           ├── colors.xml                     # System bar colors
│   │   │           ├── strings.xml                    # 100% localized application strings
│   │   │           └── themes.xml                     # Base window style
│   │   └── test/
│   │       └── java/com/example/win11calculator/
│   │           └── CalculatorEngineTest.kt            # 28 unit tests
│   └── build.gradle.kts                               # App build script (Compose BOM, SDK 34)
├── build.gradle.kts                                   # Project build script
├── gradle.properties                                  # JVM & AndroidX settings
├── gradlew / gradlew.bat                              # Gradle Wrapper scripts
└── settings.gradle.kts                                # Module configuration
```

### The Pure Kotlin Calculator Engine

The `com.example.win11calculator.engine` package has been deliberately built from scratch with **zero Android framework dependencies** (`android.*`) and **zero third-party libraries**:
- Uses standard JVM / Kotlin standard primitives: `java.math.BigDecimal`, `java.math.MathContext`, `java.math.RoundingMode`, and `kotlin.math.sqrt`.
- Completely decouples arithmetic calculations from Android UI lifecycles.
- Enables lightning-fast local unit testing on standard JVMs in under 2 seconds without requiring an Android emulator or Robolectric.

---

## Building & Running in Android Studio

### Prerequisites
- **Android Studio**: Iguana, Jellyfish, Koala (2023.3+) or newer.
- **JDK**: Java 17 or higher (configured in `compileOptions` and `kotlinOptions`).
- **Android SDK**: API level 34 (Android 14).
- **Minimum Supported Device**: API level 24+ (Android 7.0 Nougat).

### Steps
1. Clone or download the repository to your local system.
2. Launch **Android Studio**.
3. Choose **File $\rightarrow$ Open...** and select the root `win11-calculator` folder.
4. Allow Gradle to sync and download the dependencies (Jetpack Compose BOM `2024.09.03`, Material 3, Lifecycle ViewModel Compose `2.8.2`).
5. Choose an Android Virtual Device (AVD) running API 24 or newer, or connect a physical Android device with USB Debugging enabled.
6. Click the green **Run** button or press `Shift + F10`.

### Command-Line Build
You can also compile debug APKs via the Gradle wrapper in your terminal:

```bash
# Windows (PowerShell or CMD)
.\gradlew.bat assembleDebug

# Linux / macOS
./gradlew assembleDebug
```

The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## Running Unit Tests

The test suite contains **28 comprehensive unit tests** in `CalculatorEngineTest.kt` verifying:
- Basic and chained arithmetic operations (left-to-right evaluation).
- Unary operations (`²√x`, `x²`, `1/x`, `+/-`) and nested unary operations.
- Operator chaining after unary operations, `%`, and memory recalls.
- Contextual percentage calculations matching Windows 11.
- Immediate operator replacement.
- Division by zero, negative square root, and floating-point overflow errors.
- Repeat equals calculations.
- Memory store, recall, add, subtract, and clear operations.
- Number formatting, precision retention, and scientific notation.
- History recording, restoration, and clearance.

### Running via Android Studio
1. In the Project pane, navigate to `app/src/test/java/com/example/win11calculator/CalculatorEngineTest.kt`.
2. Right-click the file and click **Run 'CalculatorEngineTest'** (or press `Ctrl + Shift + F10`).

### Running via Command Line
Run the unit test task with the Gradle wrapper:

```bash
# Windows
.\gradlew.bat testDebugUnitTest

# Linux / macOS
./gradlew testDebugUnitTest
```

Once execution completes, view the detailed HTML report at:
`app/build/reports/tests/testDebugUnitTest/index.html`

---

## How to Add a New Language (Localization)

All UI strings, accessibility content descriptions, and error notifications are centralized inside `app/src/main/res/values/strings.xml`. To add support for another language:

### Step 1: Create the Localized Values Directory
In `app/src/main/res/`, create a new folder named `values-<locale>`, where `<locale>` is the ISO 639-1 language code (and optional region code):
- **Spanish**: `app/src/main/res/values-es/`
- **French**: `app/src/main/res/values-fr/`
- **German**: `app/src/main/res/values-de/`
- **Japanese**: `app/src/main/res/values-ja/`
- **Simplified Chinese**: `app/src/main/res/values-zh-rCN/`
- **Hindi**: `app/src/main/res/values-hi/`

### Step 2: Create `strings.xml` in the New Directory
Create `strings.xml` inside your new directory and translate the text strings. For example, for **Spanish** (`values-es/strings.xml`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Calculadora</string>
    <string name="title_standard">Estándar</string>

    <!-- Content descriptions for top bar -->
    <string name="cd_menu">Abrir menú de navegación</string>
    <string name="cd_history">Abrir historial de cálculos</string>
    <string name="cd_keep_on_top">Mantener en primer plano</string>

    <!-- History Panel -->
    <string name="history_title">Historial</string>
    <string name="history_empty">Aún no hay historial</string>
    <string name="clear_history">Borrar todo el historial</string>

    <!-- Memory row button labels -->
    <string name="btn_mc">MC</string>
    <string name="btn_mr">MR</string>
    <string name="btn_m_plus">M+</string>
    <string name="btn_m_minus">M−</string>
    <string name="btn_ms">MS</string>
    <string name="btn_m_view">M˅</string>

    <!-- Memory accessibility -->
    <string name="cd_mc">Borrar toda la memoria</string>
    <string name="cd_mr">Recuperar memoria</string>
    <string name="cd_m_plus">Sumar a memoria</string>
    <string name="cd_m_minus">Restar de memoria</string>
    <string name="cd_ms">Guardar en memoria</string>
    <string name="cd_m_view">Ver memoria</string>

    <!-- Calculator grid button labels -->
    <string name="btn_percent">%</string>
    <string name="btn_ce">CE</string>
    <string name="btn_c">C</string>
    <string name="btn_backspace">⌫</string>

    <string name="btn_fraction">1/x</string>
    <string name="btn_square">x²</string>
    <string name="btn_sqrt">²√x</string>
    <string name="btn_divide">÷</string>

    <string name="btn_7">7</string>
    <string name="btn_8">8</string>
    <string name="btn_9">9</string>
    <string name="btn_multiply">×</string>

    <string name="btn_4">4</string>
    <string name="btn_5">5</string>
    <string name="btn_6">6</string>
    <string name="btn_minus">−</string>

    <string name="btn_1">1</string>
    <string name="btn_2">2</string>
    <string name="btn_3">3</string>
    <string name="btn_plus">+</string>

    <string name="btn_negate">+/-</string>
    <string name="btn_0">0</string>
    <string name="btn_dot">.</string>
    <string name="btn_equals">=</string>

    <!-- Calculator accessibility descriptions -->
    <string name="cd_percent">Porcentaje</string>
    <string name="cd_ce">Borrar entrada</string>
    <string name="cd_c">Borrar todo</string>
    <string name="cd_backspace">Retroceso</string>
    <string name="cd_fraction">Recíproco</string>
    <string name="cd_square">Elevar al cuadrado</string>
    <string name="cd_sqrt">Raíz cuadrada</string>
    <string name="cd_divide">Dividir</string>
    <string name="cd_multiply">Multiplicar</string>
    <string name="cd_minus">Menos</string>
    <string name="cd_plus">Más</string>
    <string name="cd_equals">Igual</string>
    <string name="cd_negate">Cambiar signo</string>
    <string name="cd_dot">Punto decimal</string>

    <!-- Error Messages -->
    <string name="error_divide_by_zero">No se puede dividir entre cero</string>
    <string name="error_invalid_input">Entrada no válida</string>
    <string name="error_overflow">Desbordamiento</string>
</resources>
```

### Step 3: Test the Translation
- In Android Studio, open any Compose preview or `CalculatorDisplay.kt` and select your new language in the Locale dropdown.
- Alternatively, run the app on an emulator/device and switch the system language in **Settings $\rightarrow$ System $\rightarrow$ Languages**. The app will immediately adopt the new strings without requiring a rebuild.

---

## GitHub Repository Description

```text
A faithful Windows 11 Standard Calculator for Android in Kotlin & Jetpack Compose, featuring a pure-Kotlin calculation engine, exact left-to-right & unary chaining logic, memory & history panels, error handling, and fluent light/dark themes.
```
