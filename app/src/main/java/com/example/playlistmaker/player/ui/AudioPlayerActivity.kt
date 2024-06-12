package com.example.playlistmaker.player.ui
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.KEY_FOR_PLAYLIST
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val UPDATE_TIME_INFO = 500L
    }

    private var playerState = STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()
    private var mainThreadHandler: Handler? = null

    private val cycleRunnable = object : Runnable {
        override fun run() {
            val formattedTime =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            Log.d("TIME", formattedTime)
            timing.text = formattedTime
            mainThreadHandler?.postDelayed(this, UPDATE_TIME_INFO)
        }
    }

    private var url: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_audio_player)

        mainThreadHandler = Handler(Looper.getMainLooper())
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
            playbackControl()
        }

        val value: String? = intent.getStringExtra(KEY_FOR_PLAYLIST)
        val track: Track? = Gson().fromJson(value, Track::class.java)

        if (track != null) {

            val formattedTime =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
            val artworkHiResolution = track.artworkUrl.replaceAfterLast('/', "512x512bb.jpg")

            Glide.with(this)
                .load(artworkHiResolution)
                .placeholder(R.drawable.placeholder_big)
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.medium_padding)))
                .into(artworkImage)

            trackName.text = track.trackName
            artistName.text = track.artistName
            duration.text = formattedTime

            if (!track.collectionName.isNullOrEmpty()) {
                collectionName.text = track.collectionName
            } else {
                collectionName.text = "n/a"
            }

            try {
                year.text = track.releaseDate.split("-", limit = 2)[0]
            } catch (e: Exception) {
                year.text = "n/a"
            }

            genre.text = track.primaryGenreName
            country.text = track.country

            url = track.previewUrl

            preparePlayer()
        }
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        mainThreadHandler?.removeCallbacks(cycleRunnable)
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        timing.text = getString(R.string.time)
        mediaPlayer.apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            setOnCompletionListener {
                playerState = STATE_PREPARED

                mainThreadHandler?.removeCallbacks(cycleRunnable)
                timing.text = getString(R.string.time)

                play.setImageResource(R.drawable.play)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING

        mainThreadHandler?.postDelayed(cycleRunnable, UPDATE_TIME_INFO)

    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED

        mainThreadHandler?.removeCallbacks(cycleRunnable)

    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PAUSED, STATE_PREPARED -> startPlayer()
        }
    }
}