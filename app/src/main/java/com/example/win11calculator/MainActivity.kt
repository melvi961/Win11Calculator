package com.example.win11calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.win11calculator.ui.CalculatorScreen
import com.example.win11calculator.ui.theme.Win11CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Win11CalculatorTheme {
                CalculatorScreen()
            }
        }
    }
}
