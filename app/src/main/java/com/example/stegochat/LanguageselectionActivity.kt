package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class LanguageselectionActivity : AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.languageselection)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val radioGroup = findViewById<RadioGroup>(R.id.languageRadioGroup)
        val applyButton = findViewById<Button>(R.id.apply_button)

        // Back Button Click
        backButton?.setOnClickListener {
            finish() // Go back to previous activity
        }

        // Preselect saved language
        when (prefs.getString("app_language", "en")) {
            "en" -> radioGroup?.check(R.id.langEnglish)
            "hi" -> radioGroup?.check(R.id.langHindi)
            "ta" -> radioGroup?.check(R.id.langTamil)
            "kn" -> radioGroup?.check(R.id.langKannada)
            "te" -> radioGroup?.check(R.id.langTelugu)
            "ml" -> radioGroup?.check(R.id.langMalayalam)
            "bn" -> radioGroup?.check(R.id.langBengali)
            "gu" -> radioGroup?.check(R.id.langGujarati)
        }

        // Apply button saves selection and returns
        applyButton?.setOnClickListener {
            val selectedId = radioGroup?.checkedRadioButtonId ?: -1
            if (selectedId == -1) {
                Toast.makeText(this, "Select a language", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val code = when (selectedId) {
                R.id.langEnglish -> "en"
                R.id.langHindi -> "hi"
                R.id.langTamil -> "ta"
                R.id.langKannada -> "kn"
                R.id.langTelugu -> "te"
                R.id.langMalayalam -> "ml"
                R.id.langBengali -> "bn"
                R.id.langGujarati -> "gu"
                else -> "en"
            }
            prefs.edit().putString("app_language", code).apply()
            Toast.makeText(this, "Language updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}