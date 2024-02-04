package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val buttonSearch: Button = findViewById(R.id.button_search)
        val buttonMedia: Button = findViewById(R.id.button_media)
        val buttonSettings: Button = findViewById(R.id.button_settings)

        buttonSearch.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                showToast("Search button clicked")
            }
        })

        buttonMedia.setOnClickListener {
            showToast("Media button clicked")
        }

        buttonSettings.setOnClickListener {
            showToast("Settings button clicked")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}