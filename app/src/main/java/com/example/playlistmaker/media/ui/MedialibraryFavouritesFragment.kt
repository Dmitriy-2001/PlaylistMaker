package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavouritesLibraryBinding
import com.example.playlistmaker.media.presentation.MedialibraryFavouritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MedialibraryFavouritesFragment : Fragment() {

    private val viewModel: MedialibraryFavouritesViewModel by viewModel()

    private var _binding: FragmentFavouritesLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(): MedialibraryFavouritesFragment {
            return MedialibraryFavouritesFragment()
        }
    }

}