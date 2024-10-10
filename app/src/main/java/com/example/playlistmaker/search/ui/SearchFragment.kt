// SearchActivity.kt
package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.KEY_FOR_PLAYER
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.root.listeners.BottomNavigationListener
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.SearchingViewModel
import com.example.playlistmaker.search.ui.models.TracksState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment: Fragment() {
    private var bottomNavigationListener: BottomNavigationListener? = null

    private var _binding: FragmentSearchBinding?=null
    private val binding get() = _binding!!

    private var textFromSearchWidget = ""
    private val viewModel by viewModel<SearchingViewModel>()

    companion object {
        const val EDIT_TEXT_VALUE = "EDIT_TEXT_VALUE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true

    private val adapter = TrackAdapter {
        if (clickDebounce()) {
            clickToTrackList(it)
        }
    }

    private val historyAdapter = TrackAdapter {
        if (clickDebounce()) {
            clickToHistoryTrackList(it)
        }
    }

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var notFoundWidget: LinearLayout
    private lateinit var badConnectionWidget: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var badConnectionTextView: TextView
    private lateinit var historyWidget: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavigationListener) {
            bottomNavigationListener = context
        } else {
            throw IllegalArgumentException("")
        }
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavigationListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputEditText = binding.inputEditText
        clearButton = binding.clearIcon
        notFoundWidget = binding.notFoundWidget
        badConnectionWidget = binding.badConnectionWidget
        updateButton = binding.updateButton
        badConnectionTextView = binding.badConnection
        historyWidget = binding.historyWidget
        clearHistoryButton = binding.clearHistoryButton
        progressBar = binding.progressBar

        if (savedInstanceState != null) {
            inputEditText.setText(savedInstanceState.getString(EDIT_TEXT_VALUE, ""))
            textFromSearchWidget = savedInstanceState.getString(EDIT_TEXT_VALUE, "")
        }

        viewModel.tracksState.observe(viewLifecycleOwner) { tracksState ->
            render(tracksState)
        }

        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        historyRecyclerView = binding.historyRecycleView
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = historyAdapter

        viewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            historyAdapter.tracks = historyList
            historyAdapter.notifyDataSetChanged()
            showHistoryWidget()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistoryList()
            historyAdapter.notifyDataSetChanged()
            historyWidget.visibility = View.GONE
        }

        // On Focus Actions
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && viewModel.getHistoryList().isNotEmpty()) {
                historyWidget.visibility = View.VISIBLE
            } else {
                historyWidget.visibility = View.GONE
            }
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
            historyWidget.visibility = View.VISIBLE
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        updateButton.setOnClickListener {
            viewModel.searchRequest(inputEditText.text.toString())
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                textFromSearchWidget = inputEditText.text.toString()

                if (s.isNullOrEmpty()) {
                    adapter.tracks.clear()
                    adapter.notifyDataSetChanged()
                    historyWidget.visibility = View.VISIBLE
                } else {
                    historyWidget.visibility = View.GONE
                }

                viewModel.searchDebounce(changedText = s?.toString() ?: "")
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        inputEditText.addTextChangedListener(textWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchRequest(inputEditText.text.toString())
                true
            }
            false
        }

        KeyboardVisibilityEvent.setEventListener(
            activity = requireActivity(),
            lifecycleOwner = viewLifecycleOwner,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) {
                        onKeyboardVisibilityChanged(true)
                    } else {
                        onKeyboardVisibilityChanged(false)
                    }
                }
            }
        )

        // Refresh track state if input is empty
        if (inputEditText.text.toString().isEmpty()) {
            viewModel.refreshTrackState()
        }
    }


    override fun onStop() {
        super.onStop()
        viewModel.saveHistoryList()
    }

    override fun onDestroyView() {
        viewModel.onDestroy()
        super.onDestroyView()
        _binding = null
    }
    override fun onPause() {
        super.onPause()
        if (inputEditText.text.toString().isEmpty()) {
            viewModel.refreshTrackState()
        }
        textFromSearchWidget = inputEditText.text.toString() // Сохранение текста поиска
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_VALUE, textFromSearchWidget)
    }



    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clickToTrackList(track: Track) {
        viewModel.addTrackToHistoryList(track)

        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(KEY_FOR_PLAYER, track)
        startActivity(intent)
    }

    private fun clickToHistoryTrackList(track: Track) {
        viewModel.transferTrackToTop(track)

        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(KEY_FOR_PLAYER, track)
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
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
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
        } else {
            notFoundWidget.visibility = View.GONE
            badConnectionWidget.visibility = View.GONE
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }

        return current
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun render(tracksState: TracksState) {
        when {
            tracksState.isLoading -> showLoading(true)
            else -> {
                showLoading(false)
                if (tracksState.isFailed != null) {
                    when {
                        tracksState.isFailed -> showPlaceholder(false, getString(R.string.connect_error))
                        else -> showPlaceholder(false, getString(R.string.no_internet))
                    }
                } else {
                    if (tracksState.tracks?.isEmpty() == true && textFromSearchWidget.isNotEmpty()) {
                        showPlaceholder(true)
                    } else {
                        adapter.tracks.clear()
                        // Добавляем проверку на null
                        tracksState.tracks?.let {
                            adapter.tracks.addAll(it)
                        }
                        adapter.notifyDataSetChanged()
                        showPlaceholder(null)
                    }
                }
            }
        }
        showHistoryWidget()
    }


    private fun showLoading(isLoaded: Boolean) {
        progressBar.visibility = if (isLoaded) View.VISIBLE else View.GONE
    }

    private fun showHistoryWidget() {
        if (viewModel.getHistoryList().isNotEmpty() && inputEditText.text.isEmpty()) {
            historyWidget.visibility = View.VISIBLE
        } else {
            historyWidget.visibility = View.GONE
        }
    }
    private fun onKeyboardVisibilityChanged(isVisible: Boolean) {
        if (isVisible) {
            bottomNavigationListener?.toggleBottomNavigationViewVisibility(false)
        } else {
            bottomNavigationListener?.toggleBottomNavigationViewVisibility(true)
        }
    }
}