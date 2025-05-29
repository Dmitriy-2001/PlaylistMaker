package com.example.playlistmaker.media.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.presentation.NewPlaylistViewModel
import com.example.playlistmaker.root.listeners.BottomNavigationListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private companion object {
        private const val TAG = "NewPlaylistFragment"
    }

    private lateinit var toastPlaylistName: String
    private val newPlaylistViewModel by viewModel<NewPlaylistViewModel>()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var isImageAdd: Boolean = false

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private var bottomNavigationListener: BottomNavigationListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavigationListener) {
            bottomNavigationListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavigationListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        requireActivity().window
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bottomNavigationListener?.toggleBottomNavigationViewVisibility(false)

        val playlistNameEditText =
            binding.playlistName.findViewById<TextInputEditText>(R.id.playlistName)

        val playlistDescriptionEditText =
            binding.playlistDescription.findViewById<TextInputEditText>(R.id.playlistDescription)

        playlistNameEditText.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylist.isEnabled = !text.isNullOrBlank()
            newPlaylistViewModel.setPlaylistName(text.toString())
            toastPlaylistName = text.toString()
        }

        playlistDescriptionEditText.doOnTextChanged { text, _, _, _ ->
            newPlaylistViewModel.setPlaylistDescroption(text.toString())
        }

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.playlistImage.setImageURI(uri)
                val uriLocal = newPlaylistViewModel.saveImageToLocalStorage(uri)
                newPlaylistViewModel.setUri(uriLocal)
                isImageAdd = true
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        val requestPremissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d(TAG, "Permission Granted")
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    Log.d(TAG, "No permission")
                }
            }
        confirmDialog =
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.finish_creating_playlist)
                .setMessage(R.string.loss_of_unsaved_data)
                .setNeutralButton(R.string.cancel) { dialog, which -> }
                .setPositiveButton(R.string.finish) { dialog, which ->
                    findNavController().navigateUp()
                }


        binding.playlistImage.setOnClickListener {
//            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionGranted = ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_MEDIA_IMAGES
                )
                if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                    requestPremissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                } else pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                val permissionGranted = ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                )
                if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                    requestPremissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        binding.back.setOnClickListener {
            onBackPressed(playlistNameEditText, playlistDescriptionEditText)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed(playlistNameEditText, playlistDescriptionEditText)
                }
            })

        binding.createPlaylist.setOnClickListener {
            newPlaylistViewModel.createPlaylist()
            findNavController().navigateUp()
            Toast.makeText(
                requireContext(),
                "Плейлист $toastPlaylistName создан",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomNavigationListener?.toggleBottomNavigationViewVisibility(true)
    }

    private fun onBackPressed(
        playlistNameEditText: TextInputEditText,
        playlistDescriptionEditText: TextInputEditText,
    ) {
        if (isImageAdd || !playlistNameEditText.text.isNullOrBlank() || !playlistDescriptionEditText.text.isNullOrBlank()) {
            confirmDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }
}