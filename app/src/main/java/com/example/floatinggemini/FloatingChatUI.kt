package com.example.floatinggemini

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FloatingChatUI(onClose: () -> Unit, onSend: (String) -> Unit, onResize: (Float) -> Unit) {
    var input by remember { mutableStateOf("") }
    var sizeFactor by remember { mutableStateOf(1f) }

    Column(modifier = Modifier.padding(8.dp).fillMaxSize()) {
        Row {
            Button(onClick = { onClose() }) { Text("Close") }
            Button(onClick = { sizeFactor = if (sizeFactor < 1.5f) sizeFactor + 0.25f else 1f; onResize(sizeFactor) }) { Text("Resize") }
        }
        Text("Chat with Gemini (Floating)")

        Spacer(Modifier.weight(1f))

        OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            if (input.isNotBlank()) {
                onSend(input)
                input = ""
            }
        }) { Text("Send") }
    }
}