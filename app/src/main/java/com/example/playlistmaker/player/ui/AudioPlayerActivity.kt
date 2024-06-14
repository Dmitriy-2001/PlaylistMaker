package com.example.playlistmaker.player.ui

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.models.PlayerTrack
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.presentation.PlayerViewModelFactory
import com.example.playlistmaker.player.presentation.STATE_PAUSED
import com.example.playlistmaker.player.presentation.STATE_PLAYING
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.KEY_FOR_PLAYER
import java.io.Serializable

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var artworkImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var collectionName: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var play: ImageView
    private lateinit var timing: TextView

    private lateinit var viewModel: PlayerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_audio_player)

        backArrow = findViewById(R.id.back)
        artworkImage = findViewById(R.id.artwork)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artistName)
        duration = findViewById(R.id.trackTimeName)
        collectionName = findViewById(R.id.collectionName)
        year = findViewById(R.id.yearName)
        genre = findViewById(R.id.genreName)
        country = findViewById(R.id.countryName)
        play = findViewById(R.id.play)
        timing = findViewById(R.id.timing)

        backArrow.setOnClickListener {
            finish()
        }

        play.setOnClickListener {
            viewModel.playbackControl()
        }

        val track = intent.getSerializable(KEY_FOR_PLAYER, Track::class.java)

        viewModel = ViewModelProvider(this, PlayerViewModelFactory(convertTrackToPlayerTrack(track)))[PlayerViewModel::class.java]

        viewModel.playerTrackForRender.observe(this) { playerTrack ->
            render(playerTrack)
        }

        viewModel.playerState.observe(this) { statePlaying ->

            when (statePlaying) {
                STATE_PLAYING -> play.setImageResource(R.drawable.pause)
                STATE_PAUSED -> play.setImageResource(R.drawable.play)
            }
        }

        viewModel.isCompleted.observe(this) { isCompleted ->
            if (isCompleted) {
                timing.text = getString(R.string.time)
                play.setImageResource(R.drawable.play)
            }
        }

        viewModel.formattedTime.observe(this) { trackTime ->
            timing.text = trackTime
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        viewModel.release()
        super.onDestroy()

    }

    fun <T : Serializable?> Intent.getSerializable(key: String, m_class: Class<T>): T {
        return if (SDK_INT >= TIRAMISU)
            this.getSerializableExtra(key, m_class)!!
        else
            this.getSerializableExtra(key) as T
    }
    private fun convertTrackToPlayerTrack(track: Track): PlayerTrack {
        return PlayerTrack(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl = track.artworkUrl,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    private fun render(track: PlayerTrack) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = track.trackTime

        if (!track.collectionName.isNullOrEmpty()) {
            collectionName.text = track.collectionName
        } else {
            collectionName.text = getString(R.string.unknown)
        }

        year.text = track.releaseDate
        genre.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(track.artworkUrl)
            .placeholder(R.drawable.placeholder_big)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.two)))
            .into(artworkImage)
    }
}