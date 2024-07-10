package com.example.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var backArrowImageView: MaterialToolbar
    private lateinit var shareButton: MaterialTextView
    private lateinit var supportButton: MaterialTextView
    private lateinit var agreementButton: MaterialTextView
    private lateinit var themeSwitcher: SwitchMaterial

    private val viewModel by viewModel<SettingsViewModel>()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        backArrowImageView = findViewById(R.id.settings_back_button)
        shareButton = findViewById(R.id.shareButton)
        supportButton = findViewById(R.id.supportButton)
        agreementButton = findViewById(R.id.agreementButton)
        themeSwitcher = findViewById(R.id.switchDarkTheme)

        themeSwitcher.isChecked = viewModel.getThemeState()

        shareButton.setOnClickListener {
            val message = viewModel.getLinkToCourse()

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val message = viewModel.getEmailMessage()
            val subject = viewModel.getEmailSubject()
            val mailArray = viewModel.getArrayOfEmailAddresses()

            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, mailArray)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        agreementButton.setOnClickListener {
            val url = viewModel.getPracticumOffer()

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }

        backArrowImageView.setOnClickListener {
            finish()
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.saveAndChangeThemeState(checked)
        }
    }
}