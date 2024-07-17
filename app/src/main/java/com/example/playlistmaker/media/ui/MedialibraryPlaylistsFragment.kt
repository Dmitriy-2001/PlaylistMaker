package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsLibraryBinding
import com.example.playlistmaker.media.presentation.MedialibraryPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MedialibraryPlaylistsFragment : Fragment() {

    private val viewModel: MedialibraryPlaylistsViewModel by viewModel()

    private var _binding: FragmentPlaylistsLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(): MedialibraryPlaylistsFragment {
            return MedialibraryPlaylistsFragment()
        }
    }
}