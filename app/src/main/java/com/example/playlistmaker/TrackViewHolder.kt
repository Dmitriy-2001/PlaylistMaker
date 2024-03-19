package com.example.playlistmaker

import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale
import java.text.SimpleDateFormat


class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val trackName: TextView = itemView.findViewById(R.id.trackName)
    val artistName: TextView = itemView.findViewById(R.id.artistName)
    val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    val artworkUrl100: ImageView = itemView.findViewById(R.id.trackImage)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text =  SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.search)
            .centerCrop()
            .transform(RoundedCorners(Utils.dpToPx(2)))
            .into(artworkUrl100)
    }
}


