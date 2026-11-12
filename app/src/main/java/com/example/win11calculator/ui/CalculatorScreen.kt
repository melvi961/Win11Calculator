package com.example.win11calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.win11calculator.ui.components.CalculatorButtonGrid
import com.example.win11calculator.ui.components.CalculatorDisplay
import com.example.win11calculator.ui.components.CalculatorTopBar
import com.example.win11calculator.ui.components.HistoryBottomSheet
import com.example.win11calculator.ui.components.MemoryRow
import com.example.win11calculator.ui.theme.LocalWin11Colors
import com.example.win11calculator.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalWin11Colors.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
        containerColor = colors.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Bar: Hamburger, "Standard", History Icon
            CalculatorTopBar(
                onToggleHistory = viewModel::toggleHistory
            )

            // Display Area: Expression line & Large result line
            CalculatorDisplay(
                expression = uiState.expression,
                display = uiState.display,
                isError = uiState.isError,
                errorStringRes = uiState.errorStringRes,
                modifier = Modifier.weight(0.9f)
            )

            // Memory Row: MC, MR, M+, M-, MS, M˅
            MemoryRow(
                isMemoryActive = uiState.isMemoryActive,
                onMemoryClear = viewModel::onMemoryClear,
                onMemoryRecall = viewModel::onMemoryRecall,
                onMemoryAdd = viewModel::onMemoryAdd,
                onMemorySubtract = viewModel::onMemorySubtract,
                onMemoryStore = viewModel::onMemoryStore
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Button Grid: 6 rows x 4 columns
            CalculatorButtonGrid(
                onDigit = viewModel::onDigit,
                onDecimal = viewModel::onDecimal,
                onOperator = viewModel::onOperator,
                onEquals = viewModel::onEquals,
                onSquareRoot = viewModel::onSquareRoot,
                onSquare = viewModel::onSquare,
                onFraction = viewModel::onFraction,
                onPercent = viewModel::onPercent,
                onBackspace = viewModel::onBackspace,
                onClear = viewModel::onClear,
                onClearEntry = viewModel::onClearEntry,
                onNegate = viewModel::onNegate,
                modifier = Modifier.weight(2.4f)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // History Modal Bottom Sheet
        HistoryBottomSheet(
            isOpen = uiState.isHistoryOpen,
            history = uiState.history,
            onDismissRequest = { viewModel.setHistoryOpen(false) },
            onSelectHistoryItem = viewModel::onSelectHistoryItem,
            onClearHistory = viewModel::onClearHistory
        )
    }
}
