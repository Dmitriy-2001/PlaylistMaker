package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.KEY_FOR_PLAYER
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouritesLibraryBinding
import com.example.playlistmaker.media.presentation.MedialibraryFavouritesViewModel
import com.example.playlistmaker.root.listeners.BottomNavigationListener
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MedialibraryFavouritesFragment : Fragment() {

    private val viewModel: MedialibraryFavouritesViewModel by viewModel()
    private var _binding: FragmentFavouritesLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? BottomNavigationListener)?.toggleBottomNavigationViewVisibility(true)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter { track ->
            openAudioPlayer(track)
        }

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.favoriteTracks.collectLatest { tracks ->
                if (isAdded && _binding != null) { // Проверка на валидность фрагмента
                    updateUI(tracks)
                }
            }
        }
    }

    private fun updateUI(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            binding.imageView.visibility = View.VISIBLE
            binding.emptyTextView.visibility = View.VISIBLE
            binding.favoritesRecyclerView.visibility = View.GONE
        } else {
            binding.imageView.visibility = View.GONE
            binding.emptyTextView.visibility = View.GONE
            binding.favoritesRecyclerView.visibility = View.VISIBLE
            adapter.tracks = ArrayList(tracks)
            adapter.notifyDataSetChanged()
        }
    }

    private fun openAudioPlayer(track: Track) {
        val updatedTrack = track.copy(isFavorite = true) // Убедимся, что поле isFavorite обновлено
        val bundle = Bundle().apply {
            putString(KEY_FOR_PLAYER, Gson().toJson(updatedTrack))
        }

        findNavController().navigate(R.id.action_medialibraryFavouritesFragment_to_playerFragment, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): MedialibraryFavouritesFragment {
            return MedialibraryFavouritesFragment()
        }
    }
}
