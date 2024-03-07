package com.maloy.muzza.playback.queues

import androidx.media3.common.MediaItem
import com.maloy.innertube.YouTube
import com.maloy.innertube.models.WatchEndpoint
import com.maloy.muzza.extensions.toMediaItem
import com.maloy.muzza.models.MediaMetadata
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class YouTubeQueue(
    private var endpoint: WatchEndpoint,
    override val preloadItem: MediaMetadata? = null,
) : Queue {
    private var continuation: String? = null

    override suspend fun getInitialStatus(): Queue.Status {
        val nextResult = withContext(IO) {
            YouTube.next(endpoint, continuation).getOrThrow()
        }
        endpoint = nextResult.endpoint
        continuation = nextResult.continuation
        return Queue.Status(
            title = nextResult.title,
            items = nextResult.items.map { it.toMediaItem() },
            mediaItemIndex = nextResult.currentIndex ?: 0
        )
    }

    override fun hasNextPage(): Boolean = continuation != null

    override suspend fun nextPage(): List<MediaItem> {
        val nextResult = withContext(IO) {
            YouTube.next(endpoint, continuation).getOrThrow()
        }
        endpoint = nextResult.endpoint
        continuation = nextResult.continuation
        return nextResult.items.map { it.toMediaItem() }
    }
}
