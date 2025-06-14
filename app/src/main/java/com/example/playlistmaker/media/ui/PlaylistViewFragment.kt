package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.KEY_FOR_PLAYER
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCollectionBinding
import com.example.playlistmaker.media.domain.model.IntentKeys.PLAYLIST_ID_KEY
import com.example.playlistmaker.media.domain.model.Playlist

import com.example.playlistmaker.media.presentation.ViewPlaylistViewModel
import com.example.playlistmaker.root.listeners.BottomNavigationListener
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistViewFragment : Fragment() {

    private var _binding: FragmentPlaylistCollectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ViewPlaylistViewModel>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehaviorMoreOptions: BottomSheetBehavior<LinearLayout>
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    lateinit var confirmDialogTrack: MaterialAlertDialogBuilder
    private val gson: Gson by inject()
    private lateinit var currentPlaylist: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? BottomNavigationListener)?.toggleBottomNavigationViewVisibility(false)

        binding.overlay.visibility = View.GONE
        val adapter =
            PlaylistTracksAdapter(onTrackClick = { track: Track -> openPlayerWithTrack(track) },
                onLongTrackClick = { track: Track ->
                    confirmDialogTrack =
                        MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
                            .setTitle(getString(R.string.remove_track_question))
                            .setMessage(R.string.remove_track_question)
                            .setPositiveButton(getString(R.string.yes_option)) { _, _ ->
                                viewModel.removeTrack(track.trackId)
                                currentPlaylist.let { viewModel.getPlaylist(currentPlaylist) }
                            }.setNegativeButton(getString(R.string.no_option)) { _, _ ->
                            }
                    confirmDialogTrack.show()
                    true
                })
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        val shareClickListener = View.OnClickListener { sharePlaylist() }
        binding.buttonShare.setOnClickListener(shareClickListener)

        binding.buttonBackPlaylist.setOnClickListener { activity?.onBackPressed() }

        viewModel.playlistLiveData.observe(viewLifecycleOwner) { playlist ->
            binding.playlistName.text = playlist.playlistName
            if (playlist.playlistDescription.isNullOrEmpty()) {
                binding.playlistDescription.visibility = View.GONE
            } else {
                binding.playlistDescription.visibility = View.VISIBLE
                binding.playlistDescription.text = playlist.playlistDescription
            }
            Glide.with(requireContext()).load(playlist.uri).placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.playlistImage)
            Glide.with(requireContext()).load(playlist.uri).placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.playlistImageBottom)
            binding.playlistNameBottom.text = playlist.playlistName

            confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
                .setMessage("Хотите удалить плейлист \"${playlist.playlistName}\"?")
                .setNeutralButton("Нет") { _, _ -> }
                .setPositiveButton("Да") { _, _ ->
                    viewModel.deletePlaylist()
                    findNavController().navigateUp()
                }
            binding.editPlaylistButton.setOnClickListener {
                openPlaylist(playlist)
            }
        }
        viewModel.updatedPlaylistLiveData.observe(viewLifecycleOwner) { updatedPlaylist ->
            viewModel.getPlaylist(gson.toJson(updatedPlaylist))
        }

        viewModel.trackTimeLiveData.observe(viewLifecycleOwner) {
            binding.tracksTime.text = resources.getQuantityString(R.plurals.minutes, it.toInt(), it)
        }
        viewModel.trackListLiveData.observe(viewLifecycleOwner) {
            adapter.replaceTracks(it)
            binding.trackQuantityBottom.text = resources.getQuantityString(R.plurals.tracks, it.size, it.size)
            binding.tracksQty.text = resources.getQuantityString(R.plurals.tracks, it.size, it.size)

            if (it.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.empty_playlist_tracks_placeholder_message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet)
            .apply { state = BottomSheetBehavior.STATE_COLLAPSED }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> bottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        binding.playlistBottomSheet.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val location = IntArray(2)
                binding.buttonShare.getLocationOnScreen(location)
                bottomSheetBehavior.peekHeight =
                    view.height - (location[1] + binding.buttonShare.measuredHeight)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) view.viewTreeObserver.removeOnGlobalLayoutListener(
                    this
                )
                else view.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        })
        bottomSheetBehaviorMoreOptions = BottomSheetBehavior.from(binding.moreOptionsBottom)
            .apply { state = BottomSheetBehavior.STATE_HIDDEN }

        binding.buttonMore.setOnClickListener {
            bottomSheetBehaviorMoreOptions.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.buttonShareBottom.setOnClickListener {
            sharePlaylist()
        }

        binding.removePlaylistButton.setOnClickListener {
            confirmDialog.show()
        }

        bottomSheetBehaviorMoreOptions.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
                    else -> binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        val playlistId = arguments?.getInt(PLAYLIST_ID_KEY) ?: return
        viewModel.refreshPlaylist(playlistId)
    }


    private fun sharePlaylist() {
        val message = viewModel.getMessage()
        if (message == null) {
            Toast.makeText(
                requireContext(), getString(R.string.dont_share), Toast.LENGTH_SHORT
            ).show()
            return
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться плейлистом"))
    }

    private fun openPlayerWithTrack(track: Track) {
        val bundle = Bundle().apply {
            putString(KEY_FOR_PLAYER, gson.toJson(track))
        }
        findNavController().navigate(R.id.action_playlistViewFragment_to_playerFragment, bundle)
    }

    private fun openPlaylist(playlist: Playlist) {
        val bundle = Bundle().apply {
            putString(PLAYLIST_ID_KEY, gson.toJson(playlist))
        }
        findNavController().navigate(
            R.id.action_playlistViewFragment_to_editPlaylistFragment,
            bundle
        )
    }
}