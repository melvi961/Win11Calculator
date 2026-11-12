package com.example.win11calculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.win11calculator.R
import com.example.win11calculator.ui.theme.LocalWin11Colors

@Composable
fun MemoryRow(
    isMemoryActive: Boolean,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    onMemoryStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MemoryButton(
            text = stringResource(R.string.btn_mc),
            contentDesc = stringResource(R.string.cd_mc),
            enabled = isMemoryActive,
            onClick = onMemoryClear,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = stringResource(R.string.btn_mr),
            contentDesc = stringResource(R.string.cd_mr),
            enabled = isMemoryActive,
            onClick = onMemoryRecall,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = stringResource(R.string.btn_m_plus),
            contentDesc = stringResource(R.string.cd_m_plus),
            enabled = true,
            onClick = onMemoryAdd,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = stringResource(R.string.btn_m_minus),
            contentDesc = stringResource(R.string.cd_m_minus),
            enabled = true,
            onClick = onMemorySubtract,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = stringResource(R.string.btn_ms),
            contentDesc = stringResource(R.string.cd_ms),
            enabled = true,
            onClick = onMemoryStore,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = stringResource(R.string.btn_m_view),
            contentDesc = stringResource(R.string.cd_m_view),
            enabled = isMemoryActive,
            onClick = { /* Visual placeholder for memory dropdown */ },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MemoryButton(
    text: String,
    contentDesc: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalWin11Colors.current

    TextButton(
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = colors.textPrimary,
            disabledContentColor = colors.memoryDisabled
        ),
        modifier = modifier
            .height(36.dp)
            .semantics { this.contentDescription = contentDesc }
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
