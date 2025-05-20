package com.example.textfield_control

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TextFieldBlockerApp()
                }
            }
        }
    }
}

@Composable
fun TextFieldBlockerApp() {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    var text by remember { mutableStateOf("") }
    var uppercaseBlocked by remember { mutableStateOf(false) }
    var lowercaseBlocked by remember { mutableStateOf(false) }
    var nonAlphanumericBlocked by remember { mutableStateOf(false) }
    val statusBarHeightDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val padding = 16.dp
    val sidePadding = 24.dp
    var lastValidText by remember { mutableStateOf("") }

    val errorMessages = mutableListOf<String>()
    if (uppercaseBlocked && text.any { it.isUpperCase() }) errorMessages.add("Blokada wielkich liter!")
    if (lowercaseBlocked && text.any { it.isLowerCase() }) errorMessages.add("Blokada małych liter!")
    if (nonAlphanumericBlocked && text.any { !it.isLetterOrDigit() }) errorMessages.add("Blokada znaków innych niż alfanumeryczne!")

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
            .padding(horizontal = sidePadding, vertical = padding)
            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
    ) {
        Text(
            text = "Kontroler Pola Tekstowego",
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth().padding(top = statusBarHeightDp),
            textAlign = TextAlign.Center
        )
            //bugfixed
        TextField(
            value = text,
            onValueChange = { newValue ->
                if (newValue.length < text.length) {
                    // Użytkownik usuwa znak — pozwalamy na zmianę
                    text = newValue
                    lastValidText = newValue
                } else {
                    // Sprawdzamy OSTATNI wpisany znak
                    val lastChar = newValue.lastOrNull()

                    if (lastChar != null && (
                                (uppercaseBlocked && lastChar.isUpperCase()) ||
                                        (lowercaseBlocked && lastChar.isLowerCase() && !lastChar.isDigit()) ||
                                        (nonAlphanumericBlocked && !lastChar.isLetterOrDigit())
                                )) {
                        text = lastValidText // Odrzucamy TYLKO niedozwolony znak
                    } else {
                        lastValidText = newValue
                        text = newValue
                    }
                }
            },
            label = { Text("Wprowadź tekst") },
            modifier = Modifier.fillMaxWidth().padding(top = padding),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Przełączniki reguł:",
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth().padding(top = padding, bottom = padding),
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            SwitchRow("Blokuj wielkie litery", uppercaseBlocked) { uppercaseBlocked = it }
            Spacer(modifier = Modifier.height(12.dp))
            SwitchRow("Blokuj małe litery", lowercaseBlocked) { lowercaseBlocked = it }
            Spacer(modifier = Modifier.height(12.dp))
            SwitchRow("Blokuj znaki inne niż alfanumeryczne", nonAlphanumericBlocked) { nonAlphanumericBlocked = it }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pole na komunikaty o błędach
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    if (errorMessages.isNotEmpty()) Color.Red.copy(alpha = 0.1f) // Czerwone tło przy błędzie
                    else Color.Green.copy(alpha = 0.1f), // Zielone tło gdy nie ma błędów
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Znalezione problemy:",
                    fontSize = 18.sp,
                    color = if (errorMessages.isNotEmpty()) Color.Red else Color.Green,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (errorMessages.isNotEmpty()) {
                    errorMessages.forEach { error ->
                        Text(text = error, fontSize = 16.sp, color = Color.Black)
                    }
                } else {
                    Text(text = "Brak błędów", fontSize = 16.sp, color = Color.Black)
                }
            }
        }

    }
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)}}
