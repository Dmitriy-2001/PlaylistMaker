package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.media.db.TrackEntity
import java.text.SimpleDateFormat
import java.util.*

class FavoriteTracksAdapter(
    private val onTrackClick: (TrackEntity) -> Unit
) : RecyclerView.Adapter<FavoriteTracksAdapter.ViewHolder>() {
    private var tracks = listOf<TrackEntity>()
    fun setTracks(tracks: List<TrackEntity>) {
        this.tracks = tracks
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_track, parent, false)
        return ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onTrackClick(track) }
    }
    override fun getItemCount() = tracks.size
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.trackName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        private val coverImage: ImageView = itemView.findViewById(R.id.trackImage)
        fun bind(track: TrackEntity) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            val duration = track.duration?.toLongOrNull() ?: 0L
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration)
            Glide.with(itemView.context)
                .load(track.coverUrl)
                .placeholder(R.drawable.placeholder)
                .into(coverImage)
        }
    }
}