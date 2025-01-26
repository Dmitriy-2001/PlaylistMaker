package com.example.playlistmaker.player.ui

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.KEY_FOR_PLAYER
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.presentation.PlayerState
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.Serializable

private const val CORNER_RADIUS_DP = 8f
private const val TIME = "time"

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var playerState: PlayerState
    private lateinit var currentTime: String
    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track: Track? = intent.getSerializable(KEY_FOR_PLAYER, Track::class.java)
        val vModel: PlayerViewModel by viewModel { parametersOf(track) }
        viewModel = vModel

        if (savedInstanceState != null) {
            currentTime = savedInstanceState.getString(TIME, getString(R.string.time))
            binding.timing.text = currentTime
        }

        setupUI(track)

        binding.back.setOnClickListener {
            finish()
        }

        binding.play.setOnClickListener {
            viewModel.changeStatePlayerAfterClick()
        }

        binding.favorite.setOnClickListener {
            viewModel.onFavoriteClicked()
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

    private fun formatTrackTime(trackTime: String?): String {
        return trackTime?.toLongOrNull()?.let {
            java.text.SimpleDateFormat("mm:ss", java.util.Locale.getDefault()).format(it)
        } ?: "--:--"
    }

    private fun observeViewModel() {
        viewModel.getStatePlayerLiveData().observe(this) { newState ->
            playerState = newState
            updatePlaybackState()
        }

        viewModel.getIsFavoriteLiveData().observe(this) { isFavorite ->
            binding.favorite.setImageResource(
                if (isFavorite) R.drawable.like_2 else R.drawable.like
            )
        }
    }

    private fun updatePlaybackState() {
        binding.play.isEnabled = playerState.isPlayButtonEnabled
        binding.play.setImageResource(
            if (playerState.buttonIcon == "PLAY") R.drawable.play else R.drawable.pause
        )
        binding.timing.text = playerState.progress
    }

    fun <T : Serializable?> Intent.getSerializable(key: String, mClass: Class<T>): T {
        return if (SDK_INT >= TIRAMISU) {
            this.getSerializableExtra(key, mClass)!!
        } else {
            @Suppress("DEPRECATION")
            this.getSerializableExtra(key) as T
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
}

