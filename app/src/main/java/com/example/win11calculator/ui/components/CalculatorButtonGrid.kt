package com.example.win11calculator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.win11calculator.R
import com.example.win11calculator.engine.Operator
import com.example.win11calculator.ui.theme.LocalWin11Colors

private enum class ButtonType {
    DIGIT,
    FUNCTION,
    ACCENT
}

@Composable
fun CalculatorButtonGrid(
    onDigit: (Int) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (Operator) -> Unit,
    onEquals: () -> Unit,
    onSquareRoot: () -> Unit,
    onSquare: () -> Unit,
    onFraction: () -> Unit,
    onPercent: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onClearEntry: () -> Unit,
    onNegate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1: %  CE  C  ⌫
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalcButton(
                label = stringResource(R.string.btn_percent),
                contentDesc = stringResource(R.string.cd_percent),
                type = ButtonType.FUNCTION,
                onClick = onPercent,
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_ce),
                contentDesc = stringResource(R.string.cd_ce),
                type = ButtonType.FUNCTION,
                onClick = onClearEntry,
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_c),
                contentDesc = stringResource(R.string.cd_c),
                type = ButtonType.FUNCTION,
                onClick = onClear,
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_backspace),
                contentDesc = stringResource(R.string.cd_backspace),
                type = ButtonType.FUNCTION,
                onClick = onBackspace,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 1/x  x²  ²√x  ÷
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalcButton(
                label = stringResource(R.string.btn_fraction),
                contentDesc = stringResource(R.string.cd_fraction),
                type = ButtonType.FUNCTION,
                onClick = onFraction,
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_square),
                contentDesc = stringResource(R.string.cd_square),
                type = ButtonType.FUNCTION,
                onClick = onSquare,
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_sqrt),
                contentDesc = stringResource(R.string.cd_sqrt),
                type = ButtonType.FUNCTION,
                onClick = onSquareRoot,
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_divide),
                contentDesc = stringResource(R.string.cd_divide),
                type = ButtonType.FUNCTION,
                onClick = { onOperator(Operator.DIVIDE) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: 7  8  9  ×
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalcButton(
                label = stringResource(R.string.btn_7),
                type = ButtonType.DIGIT,
                onClick = { onDigit(7) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_8),
                type = ButtonType.DIGIT,
                onClick = { onDigit(8) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_9),
                type = ButtonType.DIGIT,
                onClick = { onDigit(9) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_multiply),
                contentDesc = stringResource(R.string.cd_multiply),
                type = ButtonType.FUNCTION,
                onClick = { onOperator(Operator.MULTIPLY) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: 4  5  6  −
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalcButton(
                label = stringResource(R.string.btn_4),
                type = ButtonType.DIGIT,
                onClick = { onDigit(4) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_5),
                type = ButtonType.DIGIT,
                onClick = { onDigit(5) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_6),
                type = ButtonType.DIGIT,
                onClick = { onDigit(6) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_minus),
                contentDesc = stringResource(R.string.cd_minus),
                type = ButtonType.FUNCTION,
                onClick = { onOperator(Operator.SUBTRACT) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 5: 1  2  3  +
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalcButton(
                label = stringResource(R.string.btn_1),
                type = ButtonType.DIGIT,
                onClick = { onDigit(1) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_2),
                type = ButtonType.DIGIT,
                onClick = { onDigit(2) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_3),
                type = ButtonType.DIGIT,
                onClick = { onDigit(3) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_plus),
                contentDesc = stringResource(R.string.cd_plus),
                type = ButtonType.FUNCTION,
                onClick = { onOperator(Operator.ADD) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 6: +/-  0  .  =
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalcButton(
                label = stringResource(R.string.btn_negate),
                contentDesc = stringResource(R.string.cd_negate),
                type = ButtonType.DIGIT,
                onClick = onNegate,
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_0),
                type = ButtonType.DIGIT,
                onClick = { onDigit(0) },
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_dot),
                contentDesc = stringResource(R.string.cd_dot),
                type = ButtonType.DIGIT,
                onClick = onDecimal,
                modifier = Modifier.weight(1f)
            )
            CalcButton(
                label = stringResource(R.string.btn_equals),
                contentDesc = stringResource(R.string.cd_equals),
                type = ButtonType.ACCENT,
                onClick = onEquals,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalcButton(
    label: String,
    type: ButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDesc: String? = null
) {
    val colors = LocalWin11Colors.current

    val (bgColor, textColor, borderColor) = when (type) {
        ButtonType.DIGIT -> Triple(colors.digitButton, colors.textPrimary, colors.digitButtonBorder)
        ButtonType.FUNCTION -> Triple(colors.functionButton, colors.textPrimary, colors.functionButtonBorder)
        ButtonType.ACCENT -> Triple(colors.accentButton, colors.accentButtonText, colors.accentButtonBorder)
    }

    val fontWeight = when (type) {
        ButtonType.DIGIT -> FontWeight.SemiBold
        ButtonType.ACCENT -> FontWeight.SemiBold
        ButtonType.FUNCTION -> FontWeight.Normal
    }

    val fontSize = when {
        label.length > 3 -> 14.sp
        type == ButtonType.DIGIT -> 19.sp
        else -> 17.sp
    }

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = textColor
        ),
        border = BorderStroke(0.5.dp, borderColor),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.5.dp,
            pressedElevation = 0.dp
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .fillMaxHeight()
            .semantics {
                this.contentDescription = contentDesc ?: label
            }
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = textColor
        )
    }
}
