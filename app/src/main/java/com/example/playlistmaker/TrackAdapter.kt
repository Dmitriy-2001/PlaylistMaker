package com.example.playlistmaker
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrackAdapter(private val onTrackClickListener: (Track) -> Unit): RecyclerView.Adapter<TrackAdapter.TrackHolder>() {
    var tracks = ArrayList<Track>()

    class TrackHolder(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.activity_track, parent, false)
    ) {
        private var artwork = itemView.findViewById<ImageView>(R.id.artwork)
        private var artistName = itemView.findViewById<TextView>(R.id.artistName)
        private var trackName = itemView.findViewById<TextView>(R.id.trackName)
        private var trackTime = itemView.findViewById<TextView>(R.id.trackTime)

        fun bind(track: Track, onTrackClickListener: (Track) -> Unit) {
            val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())

            Glide.with(itemView)
                .load(track.artworkUrl)
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.two)))
                .into(artwork)

            artistName.text = track.artistName
            trackName.text = track.trackName
            trackTime.text = formattedTime

            itemView.setOnClickListener { onTrackClickListener(track) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackHolder(parent)

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(tracks[position], onTrackClickListener)
    }

    override fun getItemCount() = tracks.size
}