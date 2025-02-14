package com.example.playlistmaker.player.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.BottomSheetViewBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.TrackWord

class BottomSheetPlaylistViewHolder(private val binding: BottomSheetViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, clickListener: ((Int, List<Int>, Playlist) -> Unit)?) {
        with(binding) {
            playlistName.text = playlist.playlistName
            tracksCount.text = playlist.tracksCount.toString() + " " +
                    TrackWord.getTrackWord(playlist.tracksCount, itemView)

            Glide.with(itemView)
                .load(playlist.uri.takeIf { !it.isNullOrBlank() } ?: R.drawable.placeholder_big)
                .placeholder(R.drawable.placeholder_big)
                .centerCrop()
                .into(playlistCover)
        }

        binding.root.setOnClickListener {
            clickListener?.invoke(adapterPosition, playlist.tracksIdInPlaylist, playlist)
        }
    }
}