package com.example.floatinggemini

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.*

class FloatingChatService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var params: WindowManager.LayoutParams? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var generativeModel: GenerativeModel

    override fun onCreate() {
        super.onCreate()
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showFloatingWindow()
    }

    private fun showFloatingWindow() {
        params = WindowManager.LayoutParams().apply {
            width = 400
            height = 600
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        floatingView = ComposeView(this).apply {
            setContent {
                FloatingChatUI(onClose = { stopSelf() }, onSend = { msg -> sendToGemini(msg) }, onResize = { newSize -> resizeWindow(newSize) })
            }
        }

        windowManager?.addView(floatingView, params)
    }

    private fun resizeWindow(sizeFactor: Float) {
        params?.width = (400 * sizeFactor).toInt()
        params?.height = (600 * sizeFactor).toInt()
        windowManager?.updateViewLayout(floatingView, params)
    }

    private fun sendToGemini(message: String) {
        scope.launch {
            try {
                val response = withTimeout(15000) {
                    generativeModel.generateContent(message)
                }
                Toast.makeText(this@FloatingChatService, response.text ?: "No response", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@FloatingChatService, "Error: ${e.message}. Check network.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { windowManager?.removeView(it) }
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}