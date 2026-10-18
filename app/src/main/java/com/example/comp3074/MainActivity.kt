package com.example.comp3074

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.comp3074.ui.theme.Comp3074Theme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fully transparent status and navigation bars (no scrim behind them)
        enableEdgeToEdge(
            // Dark icons over the white ActionBar, light icons over the black content
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            Comp3074Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PayCalculatorScreen(
                        onAboutClick = {
                            startActivity(Intent(this, AboutActivity::class.java))
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class PayResult(
    val pay: Double,
    val overtimePay: Double,
    val totalPay: Double,
    val tax: Double
)

fun calculatePay(hours: Double, hourlyRate: Double, taxRate: Double): PayResult {
    val pay: Double
    val overtimePay: Double
    if (hours <= 40) {
        pay = hours * hourlyRate
        overtimePay = 0.0
    } else {
        pay = 40 * hourlyRate
        overtimePay = (hours - 40) * hourlyRate * 1.5
    }
    val totalPay = pay + overtimePay
    val tax = pay * taxRate
    return PayResult(pay, overtimePay, totalPay, tax)
}

@Composable
fun PayCalculatorScreen(onAboutClick: () -> Unit, modifier: Modifier = Modifier) {
    var hours by rememberSaveable { mutableStateOf("") }
    var hourlyRate by rememberSaveable { mutableStateOf("") }
    var taxRate by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf<List<Double>?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val invalidInput = stringResource(R.string.error_invalid_input)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = hours,
            onValueChange = { hours = it },
            label = { Text(stringResource(R.string.label_hours)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = hourlyRate,
            onValueChange = { hourlyRate = it },
            label = { Text(stringResource(R.string.label_hourly_rate)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = taxRate,
            onValueChange = { taxRate = it },
            label = { Text(stringResource(R.string.label_tax_rate)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val h = hours.toDoubleOrNull()
                val r = hourlyRate.toDoubleOrNull()
                val t = taxRate.toDoubleOrNull()
                if (h == null || r == null || t == null || h < 0 || r < 0 || t < 0) {
                    error = invalidInput
                    result = null
                } else {
                    val p = calculatePay(h, r, t)
                    error = null
                    result = listOf(p.pay, p.overtimePay, p.totalPay, p.tax)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.button_calculate))
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        result?.let { values ->
            ResultRow(stringResource(R.string.label_pay), values[0])
            ResultRow(stringResource(R.string.label_overtime_pay), values[1])
            ResultRow(stringResource(R.string.label_total_pay), values[2])
            ResultRow(stringResource(R.string.label_tax), values[3])
        }

        Button(onClick = onAboutClick, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.button_about))
        }
    }
}

@Composable
fun ResultRow(label: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(String.format(Locale.getDefault(), "$%.2f", value))
    }
}

@Preview(showBackground = true)
@Composable
fun PayCalculatorPreview() {
    Comp3074Theme {
        PayCalculatorScreen(onAboutClick = {})
    }
}
