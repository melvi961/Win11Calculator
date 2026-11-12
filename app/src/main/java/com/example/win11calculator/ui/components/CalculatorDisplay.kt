package com.example.win11calculator.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.win11calculator.ui.theme.LocalWin11Colors

@Composable
fun CalculatorDisplay(
    expression: String,
    display: String,
    isError: Boolean,
    errorStringRes: Int?,
    modifier: Modifier = Modifier
) {
    val colors = LocalWin11Colors.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Small expression line above (e.g., "12 + 8 =")
        Text(
            text = expression.ifEmpty { " " },
            fontSize = 15.sp,
            color = colors.textSecondary,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        // Large result line below
        if (isError && errorStringRes != null) {
            Text(
                text = stringResource(errorStringRes),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            val fontSize = when {
                display.length <= 9 -> 46.sp
                display.length <= 13 -> 36.sp
                else -> 28.sp
            }

            Text(
                text = display,
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState, reverseScrolling = true)
            )
        }
    }
}
