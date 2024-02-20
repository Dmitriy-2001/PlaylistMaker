package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.content.res.Configuration

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val buttonSearch: Button = findViewById(R.id.button_search)
        val buttonMedia: Button = findViewById(R.id.button_media)
        val buttonSettings: Button = findViewById(R.id.button_settings)

        buttonSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        buttonMedia.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }
        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }

    private fun isDarkThemeEnabled(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

}