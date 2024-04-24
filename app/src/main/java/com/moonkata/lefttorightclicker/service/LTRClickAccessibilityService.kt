package com.moonkata.lefttorightclicker.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.accessibility.AccessibilityEvent
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import kotlin.random.Random
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.MotionEvent


class LTRClickAccessibilityService : AccessibilityService() {
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onServiceConnected() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layout = LinearLayout(applicationContext)
        layout.setBackgroundColor(Color.RED and 0x66FFFFFF)

        val fullW = Resources.getSystem().displayMetrics.widthPixels
        val fullH = Resources.getSystem().displayMetrics.heightPixels
        val touchW = (fullW * 0.4).toInt()
        val touchH = (fullH*0.8).toInt()

        val layoutParams = LayoutParams()
        layoutParams.apply {
            y = (fullH*0.1).toInt()
            x = 10
            width = touchW
            height = touchH
            type = LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            gravity = Gravity.TOP or Gravity.LEFT
            format = PixelFormat.TRANSPARENT
            flags = LayoutParams.FLAG_NOT_FOCUSABLE
        }

        try {
            windowManager.addView(layout, layoutParams)
        } catch (ex: Exception) {
            Log.e("LTRClick", "adding view failed", ex)
        }

        var clickX = fullW - 50
        var clickY = (fullH * 0.5).toInt()

        val random = Random(System.currentTimeMillis())
        layout.setOnTouchListener { view, event ->
            Log.i("LTRClick", "event $event")
            if (event.action == MotionEvent.ACTION_UP) {
                layout.setBackgroundColor(Color.GRAY and 0x11FFFFFF)
                click(clickX, clickY)
            }
            true
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.i("LTRClick", "accessibility event $event")
    }

    override fun onInterrupt() {
        Log.i("LTRClick", "interrupt")
    }

    private fun click(x: Int, y: Int) {
        Log.d("LTRClick", "CLICK $x, $y")
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 10, 10))
            .build()
        dispatchGesture(gestureDescription, null, null)
    }
}