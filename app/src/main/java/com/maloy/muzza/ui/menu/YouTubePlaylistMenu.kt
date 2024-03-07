package com.maloy.muzza.ui.menu

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.maloy.innertube.YouTube
import com.maloy.innertube.models.PlaylistItem
import com.maloy.innertube.models.SongItem
import com.maloy.innertube.utils.completed
import com.maloy.muzza.LocalDatabase
import com.maloy.muzza.LocalPlayerConnection
import com.maloy.muzza.R
import com.maloy.muzza.db.entities.PlaylistSongMap
import com.maloy.muzza.extensions.toMediaItem
import com.maloy.muzza.models.toMediaMetadata
import com.maloy.muzza.playback.queues.YouTubeQueue
import com.maloy.muzza.ui.component.GridMenu
import com.maloy.muzza.ui.component.GridMenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun YouTubePlaylistMenu(
    playlist: PlaylistItem,
    songs: List<SongItem> = emptyList(),
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val database = LocalDatabase.current
    val playerConnection = LocalPlayerConnection.current ?: return

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    AddToPlaylistDialog(
        isVisible = showChoosePlaylistDialog,
        onAdd = { targetPlaylist ->
            coroutineScope.launch(Dispatchers.IO) {
                var position = targetPlaylist.songCount
                songs.ifEmpty {
                    withContext(Dispatchers.IO) {
                        YouTube.playlist(playlist.id).completed().getOrNull()?.songs.orEmpty()
                    }
                }.let { songs ->
                    database.transaction {
                        songs
                            .map { it.toMediaMetadata() }
                            .onEach(::insert)
                            .forEach { song ->
                                insert(
                                    PlaylistSongMap(
                                        songId = song.id,
                                        playlistId = targetPlaylist.id,
                                        position = position++
                                    )
                                )
                            }
                    }
                }
            }
        },
        onDismiss = { showChoosePlaylistDialog = false }
    )

    GridMenu(
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 8.dp,
            end = 8.dp,
            bottom = 8.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
        )
    ) {
        playlist.playEndpoint?.let {
            GridMenuItem(
                icon = R.drawable.play,
                title = R.string.play
            ) {
                playerConnection.playQueue(YouTubeQueue(it))
                onDismiss()
            }
        }
        GridMenuItem(
            icon = R.drawable.shuffle,
            title = R.string.shuffle
        ) {
            playerConnection.playQueue(YouTubeQueue(playlist.shuffleEndpoint))
            onDismiss()
        }
        playlist.radioEndpoint?.let { radioEndpoint ->
            GridMenuItem(
                icon = R.drawable.radio,
                title = R.string.start_radio
            ) {
                playerConnection.playQueue(YouTubeQueue(radioEndpoint))
                onDismiss()
            }
        }
        GridMenuItem(
            icon = R.drawable.playlist_play,
            title = R.string.play_next
        ) {
            coroutineScope.launch {
                songs.ifEmpty {
                    withContext(Dispatchers.IO) {
                        YouTube.playlist(playlist.id).completed().getOrNull()?.songs.orEmpty()
                    }
                }.let { songs ->
                    playerConnection.playNext(songs.map { it.toMediaItem() })
                }
            }
            onDismiss()
        }
        GridMenuItem(
            icon = R.drawable.queue_music,
            title = R.string.add_to_queue
        ) {
            coroutineScope.launch {
                songs.ifEmpty {
                    withContext(Dispatchers.IO) {
                        YouTube.playlist(playlist.id).completed().getOrNull()?.songs.orEmpty()
                    }
                }.let { songs ->
                    playerConnection.addToQueue(songs.map { it.toMediaItem() })
                }
            }
            onDismiss()
        }
        GridMenuItem(
            icon = R.drawable.playlist_add,
            title = R.string.add_to_playlist
        ) {
            showChoosePlaylistDialog = true
        }
        GridMenuItem(
            icon = R.drawable.share,
            title = R.string.share
        ) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, playlist.shareLink)
            }
            context.startActivity(Intent.createChooser(intent, null))
            onDismiss()
        }
    }
}
