package com.example.floatinggemini

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var hasPermission by remember { mutableStateOf(Settings.canDrawOverlays(context)) }

            Column {
                Text("Floating Gemini Chat")
                Button(onClick = {
                    if (!hasPermission) {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    } else {
                        startService(Intent(context, FloatingChatService::class.java))
                    }
                }) {
                    Text(if (hasPermission) "Start Floating Chat" else "Grant Overlay Permission")
                }
            }
        }
    }
}