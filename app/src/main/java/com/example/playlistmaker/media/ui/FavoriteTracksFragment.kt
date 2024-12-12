package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.KEY_FOR_HISTORY_LIST
import com.example.playlistmaker.R
import com.example.playlistmaker.media.db.TrackEntity
import com.example.playlistmaker.media.presentation.FavoriteTracksViewModel
import com.example.playlistmaker.player.domain.PlayerTrack
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(): FavoriteTracksFragment {
            return FavoriteTracksFragment()
        }
    }

    private val viewModel by viewModel<FavoriteTracksViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteTracksAdapter
    private lateinit var placeholderImage: View
    private lateinit var placeholderText: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favourites_library, container, false)
        recyclerView = view.findViewById(R.id.rvTrackFavorite)
        placeholderImage = view.findViewById(R.id.imageView)
        placeholderText = view.findViewById(R.id.textView)
        adapter = FavoriteTracksAdapter { track -> openPlayerWithTrack(track) }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }
    private fun observeViewModel() {
        viewModel.favoriteTracksLiveData.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                placeholderImage.visibility = View.VISIBLE
                placeholderText.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                placeholderImage.visibility = View.GONE
                placeholderText.visibility = View.GONE
                adapter.setTracks(tracks)
            }
        }
    }
    private fun openPlayerWithTrack(track: TrackEntity) {
        val playerTrack = PlayerTrack(
            id = track.trackId,
            artworkUrl100 = track.coverUrl,
            name = track.trackName,
            artistName = track.artistName,
            collectionName = track.albumName,
            releaseDate = track.releaseYear,
            primaryGenreName = track.genre,
            country = track.country,
            timeMillis = track.duration.toLong(),
            previewUrl = track.trackUrl
        )
        val intent = Intent(context, AudioPlayerActivity::class.java).apply {
            putExtra(KEY_FOR_HISTORY_LIST, Gson().toJson(playerTrack))
        }
        startActivity(intent)
    }
}