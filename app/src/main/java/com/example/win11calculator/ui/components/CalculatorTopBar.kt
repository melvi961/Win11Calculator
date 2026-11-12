package com.example.win11calculator.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.win11calculator.R
import com.example.win11calculator.ui.theme.LocalWin11Colors

@Composable
fun CalculatorTopBar(
    onToggleHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalWin11Colors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Menu placeholder */ }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.cd_menu),
                tint = colors.textPrimary
            )
        }

        Text(
            text = stringResource(R.string.title_standard),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onToggleHistory) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = stringResource(R.string.cd_history),
                tint = colors.textPrimary
            )
        }
    }
}
