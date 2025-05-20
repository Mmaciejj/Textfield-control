package com.example.textfield_control

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    val errorMessages = mutableListOf<String>()
    if (uppercaseBlocked && text.any { it.isUpperCase() }) errorMessages.add("Blokada wielkich liter naruszona!")
    if (lowercaseBlocked && text.any { it.isLowerCase() }) errorMessages.add("Blokada małych liter naruszona!")
    if (nonAlphanumericBlocked && text.any { !it.isLetterOrDigit() }) errorMessages.add("Blokada znaków nie-alfanumerycznych naruszona!")

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


            //przepisane pole - bugfix2
        OutlinedTextField(

            value = text,
            onValueChange = { newText ->
                if (newText.length == text.length - 1) {
                    // nieograniczone uzycie backspace, ale wylaczone zaznaczanie
                    text = newText
                } else {
                    // Sprawdzam różnice między poprzednim i nowym tekstem
                    val diffIndex = newText.indices.firstOrNull { index ->
                        index >= text.length || newText[index] != text.getOrNull(index)
                    }

                    val addedChar = diffIndex?.let { newText[it] }
                    val isAllowed = addedChar?.let { char ->
                        (!uppercaseBlocked || !char.isUpperCase()) &&
                                (!lowercaseBlocked || !char.isLowerCase()) &&
                                (!nonAlphanumericBlocked || char.isLetterOrDigit())
                    } ?: true

                    if (isAllowed && newText.length <= 68) {
                        text = newText
                    }
                }


            },

            label = { Text("Wpisz tekst") },
            modifier = Modifier.fillMaxWidth().padding(top = padding),
            visualTransformation = VisualTransformation.None

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
