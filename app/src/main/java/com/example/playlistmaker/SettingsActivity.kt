package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import com.google.android.material.switchmaterial.SwitchMaterial
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
        val subject = (R.string.support_message)
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

}
