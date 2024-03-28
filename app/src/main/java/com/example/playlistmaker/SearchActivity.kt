// SearchActivity.kt
package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val TEXT_SEARCH = "TEXT_SEARCH"
    }

    private var inputText: String? = null
    private lateinit var inputEditText: EditText
    private lateinit var backArrowImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var statusLayout: LinearLayout
    private lateinit var statusImage: ImageView
    private lateinit var statusCaption: TextView
    private lateinit var statusAddText: TextView
    private lateinit var btnReload: Button
    private lateinit var clearButton: ImageView

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesAPI::class.java)
    private val trackList = ArrayList<Track>()
    private val adapter = TrackAdapter(trackList)
    private var searchHistoryList = ArrayList<Track>()
    private lateinit var historySearchText: TextView
    private lateinit var btnClearHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        clearButton = findViewById(R.id.clearIcon)
        inputEditText = findViewById(R.id.inputEditText)
        backArrowImageView = findViewById(R.id.backArrowImageView)
        recyclerView = findViewById(R.id.recyclerView)
        statusLayout = findViewById(R.id.status)
        statusImage = findViewById(R.id.status_img)
        statusCaption = findViewById(R.id.status_caption)
        statusAddText = findViewById(R.id.status_add_text)
        btnReload = findViewById(R.id.reload_btn)
        historySearchText = findViewById(R.id.history_search_text)
        btnClearHistory = findViewById(R.id.clear_history_btn)
        recyclerView = findViewById(R.id.recyclerView)

        inputEditText.setText(savedInstanceState?.getString(TEXT_SEARCH, ""))

        backArrowImageView.setOnClickListener { finish() }
        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard()
            recyclerView.visibility = View.GONE

            searchHistoryList = SearchHistory.fill()
            viewResult(
                if (searchHistoryList.size > 0)
                    TrackSearchStatus.ShowHistory
                else TrackSearchStatus.Empty
            )
        }
        btnClearHistory.setOnClickListener {
            searchHistoryList.clear()
            SearchHistory.clear()
            viewResult(TrackSearchStatus.Empty)
        }
        btnReload.setOnClickListener { iTunesSearch() }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) {
                    trackList.clear()
                    viewResult(TrackSearchStatus.Success)
                    recyclerView.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
        searchHistoryList = SearchHistory.fill()
        recyclerView.adapter = adapter
        viewResult(
            if (searchHistoryList.size > 0)
                TrackSearchStatus.ShowHistory
            else TrackSearchStatus.Empty
        )

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                iTunesSearch()
                true
            } else {
                false
            }
        }

        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(TEXT_SEARCH)
            inputEditText.setText(inputText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(TEXT_SEARCH, inputEditText.text.toString())
        super.onSaveInstanceState(outState)
    }

    private fun hideSoftKeyboard() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    private fun iTunesSearch() {
        val searchText = inputEditText.text.toString()
        if (searchText.isNotEmpty()) {
            iTunesService.search(searchText).enqueue(object : Callback<TrackResponce> {
                override fun onResponse(call: Call<TrackResponce>, response: Response<TrackResponce>) {
                    Log.d("SearchActivity", "Search request response: ${response.isSuccessful}, ${response.body()?.results}")
                    if (response.isSuccessful) {
                        val results = response.body()?.results
                        trackList.clear()
                        results?.let {
                            trackList.addAll(it)
                        }

                        if (trackList.isEmpty()) {
                            viewResult(TrackSearchStatus.NoDataFound) // Установка статуса NoDataFound, если список пуст
                        } else {
                            viewResult(TrackSearchStatus.Success)
                        }
                    } else {
                        viewResult(TrackSearchStatus.ConnectionError)
                    }
                }

                override fun onFailure(call: Call<TrackResponce>, t: Throwable) {
                    viewResult(TrackSearchStatus.ConnectionError)
                }
            })
        } else {
            viewResult(TrackSearchStatus.Empty) // Установка статуса Empty, если поле поиска пусто
        }
    }

    private fun  viewResult(status: TrackSearchStatus) {
        when (status) {
            TrackSearchStatus.Empty -> {
                statusLayout.visibility = View.GONE
                recyclerView.visibility = View.GONE
                historySearchText.visibility = View.GONE
                btnClearHistory.visibility = View.GONE
            }
            TrackSearchStatus.Success -> {
                statusLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                historySearchText.visibility = View.GONE
                btnClearHistory.visibility = View.GONE
                adapter.trackList = trackList
                adapter.notifyDataSetChanged()
            }
            TrackSearchStatus.NoDataFound -> {
                statusLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                statusImage.setImageResource(R.drawable.nodatafound)
                statusAddText.visibility = View.GONE
                btnReload.visibility = View.GONE
                statusCaption.setText(R.string.no_data_found)
                historySearchText.visibility = View.GONE
                btnClearHistory.visibility = View.GONE
            }
            TrackSearchStatus.ConnectionError -> {
                statusLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                statusImage.setImageResource(R.drawable.connect_error)
                statusAddText.visibility = View.VISIBLE
                btnReload.visibility = View.VISIBLE
                statusCaption.setText(R.string.connect_error)
                historySearchText.visibility = View.GONE
                btnClearHistory.visibility = View.GONE
            }
            TrackSearchStatus.ShowHistory -> {
                statusLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                historySearchText.visibility = View.VISIBLE
                btnClearHistory.visibility = View.VISIBLE
                adapter.trackList = searchHistoryList
                adapter.notifyDataSetChanged()
            }
        }
    }
}