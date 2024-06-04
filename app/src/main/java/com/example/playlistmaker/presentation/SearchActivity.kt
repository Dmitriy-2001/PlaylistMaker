// SearchActivity.kt
package com.example.playlistmaker.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.SHARED_PREFERENCES
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.launch

const val KEY_FOR_HISTORY_LIST = "key_for_history_list"
const val KEY_FOR_PLAYLIST = "key_for_playlist"

class SearchActivity : AppCompatActivity() {
    var textFromSearchWidget = ""

    companion object {
        const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private val tracks = ArrayList<Track>()
    private var searchHistory: SearchHistory? = null
    private lateinit var tracksInteractor: TracksInteractor
    private val adapter = TrackAdapter {
        clickToTrackList(it)
    }
    private val historyAdapter = TrackAdapter {
        if (clickDebounce()) {
            clickToHistoryTrackList(it)
        }
    }
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backArrowImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var notFoundWidget: LinearLayout
    private lateinit var badConnectionWidget: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var badConnectionTextView: TextView
    private lateinit var historyWidget: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var searchedText: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация TracksInteractor
        tracksInteractor = Creator.provideTracksInteractor(this)

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.tracks = tracks
        historyRecyclerView = findViewById(R.id.history_recycle_view)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
        historyAdapter.tracks = searchHistory!!.historyList
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        backArrowImageView = findViewById(R.id.backArrowImageView)
        notFoundWidget = findViewById(R.id.not_found_widget)
        badConnectionWidget = findViewById(R.id.bad_connection_widget)
        updateButton = findViewById(R.id.update_button)
        badConnectionTextView = findViewById(R.id.bad_connection)
        historyWidget = findViewById(R.id.history_widget)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        progressBar = findViewById(R.id.progressBar)
        searchedText = findViewById(R.id.history_text)

        clearHistoryButton.setOnClickListener {
            searchHistory!!.clearHistoryList()
            historyAdapter.notifyDataSetChanged()
            showHistoryWidget()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            tracks.clear()
            adapter.notifyDataSetChanged()
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            showPlaceholder(null)
            showHistoryWidget()
        }

        updateButton.setOnClickListener {
            search()
        }

        backArrowImageView.setOnClickListener {
            finish()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                textFromSearchWidget = inputEditText.text.toString()
                if (s.isNullOrEmpty()) {
                    showHistoryWidget()
                    recyclerView.visibility = View.GONE
                    tracks.clear()
                    notFoundWidget.visibility = View.GONE
                    badConnectionWidget.visibility = View.GONE
                } else {
                    historyWidget.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                }
                searchDebounce()
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        inputEditText.addTextChangedListener(textWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            } else {
                false
            }
        }

        showHistoryWidget()
    }

    override fun onStop() {
        super.onStop()
        searchHistory?.saveToSH(searchHistory!!.historyList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_VALUE, textFromSearchWidget)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputEditText.setText(savedInstanceState.getString(EDIT_TEXT_VALUE, ""))
        showHistoryWidget()
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clickToTrackList(track: Track) {
        searchHistory?.addTrack(track)
        historyAdapter.notifyDataSetChanged()

        val json = Gson().toJson(track)
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(KEY_FOR_PLAYLIST, json)
        startActivity(intent)
    }

    private fun clickToHistoryTrackList(track: Track) {
        val json = Gson().toJson(track)
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(KEY_FOR_PLAYLIST, json)
        startActivity(intent)
    }

    private fun showPlaceholder(flag: Boolean?, message: String = "") {
        if (flag != null) {
            if (flag) {
                badConnectionWidget.visibility = View.GONE
                notFoundWidget.visibility = View.VISIBLE
            } else {
                notFoundWidget.visibility = View.GONE
                badConnectionWidget.visibility = View.VISIBLE
                badConnectionTextView.text = message
            }
            tracks.clear()
            adapter.notifyDataSetChanged()
        } else {
            notFoundWidget.visibility = View.GONE
            badConnectionWidget.visibility = View.GONE
        }
    }

    private fun search() {
        if (inputEditText.text.toString().isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            notFoundWidget.visibility = View.GONE
            badConnectionWidget.visibility = View.GONE
            historyRecyclerView.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            searchedText.visibility = View.GONE

            Log.d("SearchActivity", "Searching for: ${inputEditText.text.toString()}")

            lifecycleScope.launch {
                tracksInteractor.searchTracks(inputEditText.text.toString(), object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>, isFailed: Boolean) {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            if (isFailed) {
                                Log.d("SearchActivity", "Search failed")
                                showPlaceholder(false, getString(R.string.connect_error))
                            } else {
                                if (foundTracks.isEmpty()) {
                                    Log.d("SearchActivity", "No tracks found")
                                    showPlaceholder(true)
                                } else {
                                    Log.d("SearchActivity", "Tracks found: $foundTracks")
                                    tracks.clear()
                                    tracks.addAll(foundTracks)
                                    adapter.notifyDataSetChanged()
                                    recyclerView.visibility = View.VISIBLE
                                    showPlaceholder(null)
                                }
                            }
                        }
                    }
                })
            }
        } else {
            showHistoryWidget()
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showHistoryWidget() {
        if (searchHistory?.historyList.isNullOrEmpty()) {
            historyWidget.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        } else {
            historyWidget.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        }
    }
}
