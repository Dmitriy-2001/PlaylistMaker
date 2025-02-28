package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.KEY_FOR_HISTORY_LIST
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.presentation.PlayerState
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.root.listeners.BottomNavigationListener
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val CORNER_RADIUS_DP = 8f
private const val TIME = "time"

class PlayerFragment : Fragment() {

    private val gson: Gson by inject()

    private lateinit var playerState: PlayerState
    private lateinit var currentTime: String
    private lateinit var viewModel: PlayerViewModel

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    // Элементы BottomSheet
    private lateinit var overlay: View
    private lateinit var newPlaylistButton: Button
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var playlistAdapter: BottomSheetPlaylistAdapter? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? BottomNavigationListener)?.toggleBottomNavigationViewVisibility(false)

        val trackJson = arguments?.getString(KEY_FOR_HISTORY_LIST)
        val track: Track? = trackJson?.let { gson.fromJson(it, Track::class.java) }
        val vModel: PlayerViewModel by viewModel { parametersOf(track) }
        viewModel = vModel


        if (savedInstanceState != null) {
            currentTime = savedInstanceState.getString(TIME, getString(R.string.time)) ?: ""
            binding.timing.text = currentTime
        }

        setupUI(track)
        setupBottomSheet()

        binding.back.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.play.setOnClickListener {
            viewModel.changeStatePlayerAfterClick()
        }

        binding.favorite.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.addToPlaylist.setOnClickListener {
            showBottomSheet()
            viewModel.getSavedPlaylists()
        }

        overlay.setOnClickListener {
            hideBottomSheet()
        }

        newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        observeViewModel()
    }

    private fun setupUI(track: Track?) {
        Glide.with(this)
            .load(track?.artworkUrl512)
            .placeholder(R.drawable.placeholder_big)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        CORNER_RADIUS_DP,
                        resources.displayMetrics
                    ).toInt()
                )
            )
            .into(binding.artwork)

        binding.trackName.text = track?.trackName
        binding.artistName.text = track?.artistName
        binding.timing.text = formatTrackTime(track?.trackTime)

        if (!track?.collectionName.isNullOrEmpty()) {
            binding.collectionName.text = track?.collectionName ?: ""
        } else {
            binding.collectionName.isVisible = false
        }

        binding.yearName.text = track?.releaseDate?.take(4)
        binding.genreName.text = track?.primaryGenreName
        binding.countryName.text = track?.country
        binding.play.isEnabled = false
    }

    private fun formatTrackTime(trackTime: Long?): String {
        return trackTime?.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(it))
        } ?: "--:--"
    }

    private fun showBottomSheet() {
        if (!::bottomSheetBehavior.isInitialized) {
            Log.e("PlayerFragment", "BottomSheet не инициализирован!")
            return
        }
        overlay.visibility = View.VISIBLE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun hideBottomSheet() {
        if (!::bottomSheetBehavior.isInitialized) return
        overlay.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }


    private fun setupBottomSheet() {
        overlay = binding.overlay
        newPlaylistButton = binding.newPlaylist

        binding.playlistsBottomSheet.visibility = View.VISIBLE
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)

        playlistAdapter = BottomSheetPlaylistAdapter().apply {
            itemClickListener = { _, tracksIdInPlaylist, playlist ->
                viewModel.getTrack()?.let { track ->
                    viewModel.addTracksIdInPlaylist(playlist, tracksIdInPlaylist, track)
                }
            }
        }


        binding.recyclerView.adapter = playlistAdapter
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    overlay.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        observePlaylists()
    }


    private fun observePlaylists() {
        viewModel.observePlaylists().observe(viewLifecycleOwner) { newPlaylists ->
            if (newPlaylists.isNullOrEmpty()) {
                binding.recyclerView.isVisible = false
                binding.placeholderMessage.isVisible = true
            } else {
                binding.recyclerView.isVisible = true
                binding.placeholderMessage.isVisible = false
                playlistAdapter?.apply {
                    this.playlists.clear()
                    this.playlists.addAll(newPlaylists)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.getStatePlayerLiveData().observe(viewLifecycleOwner) { newState ->
            playerState = newState
            updatePlaybackState()
        }

        viewModel.getIsFavoriteLiveData().observe(viewLifecycleOwner) { isFavorite ->
            binding.favorite.setImageResource(
                if (isFavorite) R.drawable.like_2 else R.drawable.like
            )
        }

        viewModel.observeTrackInPlaylistState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is TrackInPlaylistState.TrackIsAlreadyInPlaylist -> {
                    makeToast(state)
                    // Не скрываем BottomSheet
                }
                is TrackInPlaylistState.TrackAddToPlaylist -> {
                    makeToast(state)
                    hideBottomSheet() // Скрываем только если трек успешно добавлен
                }
            }
        }
    }

    private fun updatePlaybackState() {
        binding.play.isEnabled = playerState.isPlayButtonEnabled
        binding.play.setImageResource(
            if (playerState.buttonIcon == "PLAY") R.drawable.play else R.drawable.pause
        )
        binding.timing.text = playerState.progress
    }

    private fun makeToast(state: TrackInPlaylistState) {
        when (state) {
            is TrackInPlaylistState.TrackIsAlreadyInPlaylist -> Toast.makeText(
                requireContext(),
                getString(R.string.track_added_to_playlist_already) + " ${state.playlistName}",
                Toast.LENGTH_SHORT
            ).show()
            is TrackInPlaylistState.TrackAddToPlaylist -> Toast.makeText(
                requireContext(),
                getString(R.string.track_added_now) + " ${state.playlistName}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TIME, binding.timing.text.toString())
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as? BottomNavigationListener)?.toggleBottomNavigationViewVisibility(true)
    }
}
