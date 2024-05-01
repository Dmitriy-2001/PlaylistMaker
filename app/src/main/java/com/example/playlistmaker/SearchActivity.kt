// SearchActivity.kt
package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.SearchActivity.Companion.EDIT_TEXT_VALUE
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val KEY_FOR_HISTORY_LIST = "key_for_history_list"
const val KEY_FOR_PLAYLIST = "key_for_playlist"
class SearchActivity : AppCompatActivity() {
    var textFromSearchWidget = ""

    private val baseUrl = "https://itunes.apple.com"

    companion object {
        const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
    }

    val tracks = ArrayList<Track>()

    var searchHistory: SearchHistory? = null

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackService = retrofit.create(ItunesApi::class.java)

    private val adapter = TrackAdapter {
        clickToTrackList(it)
    }

    private val historyAdapter = TrackAdapter {
        clickToHistoryTrackList(it)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val sharedPreferences: SharedPreferences =
            getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
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



        clearHistoryButton.setOnClickListener {
            searchHistory!!.clearHistoryList()
            adapter.notifyDataSetChanged()
            historyWidget.visibility = View.GONE
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            tracks.clear()
            adapter.notifyDataSetChanged()
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            showPlaceholder(null)
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
                historyWidget.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory!!.historyList.isNotEmpty()) View.VISIBLE else View.GONE
                if (s.isNullOrEmpty()) {
                    // Скрываем результаты поиска
                    recyclerView.visibility = View.GONE
                    // Очищаем список треков
                    tracks.clear()
                }
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

        // Устанавливаем видимость historyWidget в VISIBLE при создании активности
        historyWidget.visibility = View.VISIBLE

        if (searchHistory?.historyList.isNullOrEmpty()) {
            historyWidget.visibility = View.GONE
        } else { historyWidget.visibility = View.VISIBLE }


    }

    override fun onStop() {
        super.onStop()
        searchHistory?.writeToSH(searchHistory!!.historyList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_VALUE, textFromSearchWidget)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputEditText.setText(savedInstanceState.getString(EDIT_TEXT_VALUE, ""))
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
            if (flag == true) {
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
        trackService.search(text = inputEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.tracks?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()?.tracks!!)
                                adapter.notifyDataSetChanged()
                                recyclerView.visibility = View.VISIBLE
                                showPlaceholder(null)
                            } else {
                                showPlaceholder(true)
                            }
                        }

                        else -> {
                            showPlaceholder(false, getString(R.string.connect_error))
                        }
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showPlaceholder(false, getString(R.string.no_internet))
                }
            })
    }
}
