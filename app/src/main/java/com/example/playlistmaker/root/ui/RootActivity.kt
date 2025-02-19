package com.example.playlistmaker.root.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.root.listeners.BottomNavigationListener
import com.google.android.material.bottomnavigation.BottomNavigationView

class RootActivity: AppCompatActivity(), BottomNavigationListener {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

    }

    override fun toggleBottomNavigationViewVisibility(visible: Boolean) {
        bottomNavigationView.isVisible = visible
        findViewById<View>(R.id.divider).isVisible = visible
    }
}