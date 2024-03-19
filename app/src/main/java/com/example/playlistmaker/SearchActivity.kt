// SearchActivity.kt
package com.example.playlistmaker

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
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

        inputEditText.setText(savedInstanceState?.getString(TEXT_SEARCH, ""))

        backArrowImageView.setOnClickListener { finish() }
        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard()
            recyclerView.visibility = View.GONE
        }
        btnReload.setOnClickListener { iTunesSearch() }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)
        recyclerView.adapter = adapter

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
                    if (response.isSuccessful) {
                        val results = response.body()?.results
                        trackList.clear()
                        results?.let {
                            trackList.addAll(it)
                        }
                        adapter.notifyDataSetChanged()

                        if (trackList.isEmpty()) {
                            viewSearchResult(TrackSearchStatus.NoDataFound)
                        } else {
                            viewSearchResult(TrackSearchStatus.Success)
                        }
                    } else {
                        viewSearchResult(TrackSearchStatus.ConnectionError)
                    }
                }

                override fun onFailure(call: Call<TrackResponce>, t: Throwable) {
                    viewSearchResult(TrackSearchStatus.ConnectionError)
                }
            })
        }
    }

    private fun viewSearchResult(status: TrackSearchStatus) {
        when (status) {
            TrackSearchStatus.Success -> {
                statusLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            TrackSearchStatus.NoDataFound -> {
                statusLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                statusImage.setImageResource(R.drawable.nodatafound)
                statusAddText.visibility = View.GONE
                btnReload.visibility = View.GONE
                statusCaption.setText(R.string.no_data_found)
            }
            TrackSearchStatus.ConnectionError -> {
                statusLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                statusImage.setImageResource(R.drawable.connect_error)
                statusAddText.visibility = View.VISIBLE
                btnReload.visibility = View.VISIBLE
                statusCaption.setText(R.string.connect_error)
            }
        }
    }
}