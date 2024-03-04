// SearchActivity.kt
package com.example.playlistmaker

import android.content.Context
import android.os.Build.VERSION_CODES.N
import android.os.Build.VERSION_CODES.O
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private var searchQuery: String? = null
    private val audio = ArrayList<Track>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

            //Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Обработчик нажатия на кнопку "Назад"
        toolbar.setNavigationOnClickListener {
            finish() // Закрываем текущую активити при нажатии "Назад"
        }

        clearButton = findViewById(R.id.clearButton)
        clearButton.setOnClickListener {
            inputEditText.text.clear()
            hideKeyboard()
            clearButton.visibility = View.GONE
        }

        inputEditText = findViewById(R.id.search_form)
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString()
            }
        })
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY)
            inputEditText.setText(searchQuery)
        }


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTracks)
        val trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
    }

}
