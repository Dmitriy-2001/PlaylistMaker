package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import android.content.res.Configuration
import android.graphics.PorterDuff

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        updateColors()

        // Настройка переключателя темной темы
        val switchDarkTheme: Switch = findViewById(R.id.switchDarkTheme)
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            recreate() // Пересоздаем активити для применения новой темы
        }
        val toolbar: Toolbar = findViewById(R.id.settings_back_button)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish() // Закрываем текущую активити при нажатии "Назад"
        }
    }

    private fun updateColors() {
        val settingsLayout: LinearLayout = findViewById(R.id.settingsLayout)
        val toolbar: Toolbar = findViewById(R.id.settings_back_button)

        if (isDarkThemeEnabled()) {
            settingsLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.blackYP))
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            val icon = ContextCompat.getDrawable(this, R.drawable.arrow_back)
            icon?.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP)
            toolbar.navigationIcon = icon
        } else {
            settingsLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
            val icon = ContextCompat.getDrawable(this, R.drawable.arrow_back)
            icon?.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP)
            toolbar.navigationIcon = icon
        }
    }

    private fun isDarkThemeEnabled(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}
