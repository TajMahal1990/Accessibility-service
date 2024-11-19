package com.example.accessibilityservice


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val enableAccessibilityButton: Button = findViewById(R.id.enableAccessibilityButton)

        enableAccessibilityButton.setOnClickListener {
            // Открываем настройки для включения службы доступности
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
    }
}
