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
import android.net.Uri
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

     //   updateColors()

        // Настройка переключателя темной темы
        val switchDarkTheme: Switch = findViewById(R.id.switchDarkTheme)
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }
        val toolbar: Toolbar = findViewById(R.id.settings_back_button)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish() // Закрываем текущую активити при нажатии "Назад"
        }

        val shareButton: FrameLayout = findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            shareApp()
        }

        val supportButton: FrameLayout = findViewById(R.id.supportButton)
        supportButton.setOnClickListener {
            writeToSupport()
        }

        val agreementButton: FrameLayout = findViewById(R.id.agreementButton)
        agreementButton.setOnClickListener {
            openUserAgreement()
        }
    }

  /*  private fun updateColors() {
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
*/
    private fun shareApp() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this Android development course at Yandex.Praktikum: https://practicum.yandex.ru/android-developer/")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

    private fun writeToSupport() {
        val recipient = "richard.orlov13@gmail.com"
        val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(emailIntent)
    }

    private fun openUserAgreement() {
        val url = "https://yandex.ru/legal/practicum_offer/"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

}
