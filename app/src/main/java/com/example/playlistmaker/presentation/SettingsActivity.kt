package com.example.playlistmaker.presentation

import android.content.Intent
import android.os.Bundle
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import android.content.res.Configuration
import com.example.playlistmaker.data.App
import com.example.playlistmaker.data.KEY_FOR_APP_THEME
import com.example.playlistmaker.R
import com.example.playlistmaker.data.SHARED_PREFERENCES

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        //Настройка темы
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.switchDarkTheme)
        if ((applicationContext as App).darkTheme) {
            themeSwitcher.setChecked(true);
        }

        themeSwitcher.setOnCheckedChangeListener {switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPreferences.edit()
                .putBoolean(KEY_FOR_APP_THEME, checked)
                .apply()
        }

        val toolbar: MaterialToolbar = findViewById(R.id.settings_back_button)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish() // Закрываем текущую активити при нажатии "Назад"
        }

        val shareButton: MaterialTextView = findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            shareApp()
        }

        val supportButton: MaterialTextView = findViewById(R.id.supportButton)
        supportButton.setOnClickListener {
            writeToSupport()
        }

        val agreementButton: MaterialTextView = findViewById(R.id.agreementButton)
        agreementButton.setOnClickListener {
            openUserAgreement()
        }
    }

    private fun shareApp() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_via)))
    }

    private fun writeToSupport() {
        val recipient = getString(R.string.my_mail)
        val subject = getString(R.string.support_message)
        val message = getString(R.string.thanks_message)
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(emailIntent)
    }

    private fun openUserAgreement() {
        val url = getString(R.string.user_agreement_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
    private fun isDarkThemeEnabled(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}

