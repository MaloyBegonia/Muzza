package com.maloy.innertube.models.response

import com.maloy.innertube.models.Button
import com.maloy.innertube.models.Continuation
import com.maloy.innertube.models.Menu
import com.maloy.innertube.models.MusicShelfRenderer
import com.maloy.innertube.models.ResponseContext
import com.maloy.innertube.models.Runs
import com.maloy.innertube.models.SectionListRenderer
import com.maloy.innertube.models.Tabs
import com.maloy.innertube.models.ThumbnailRenderer
import kotlinx.serialization.Serializable

@Serializable
data class BrowseResponse(
    val contents: Contents?,
    val continuationContents: ContinuationContents?,
    val header: Header?,
    val microformat: Microformat?,
    val responseContext: ResponseContext,
) {
    @Serializable
    data class Contents(
        val singleColumnBrowseResultsRenderer: Tabs?,
        val sectionListRenderer: SectionListRenderer?,
    )

    @Serializable
    data class ContinuationContents(
        val sectionListContinuation: SectionListContinuation?,
        val musicPlaylistShelfContinuation: MusicPlaylistShelfContinuation?,
    ) {
        @Serializable
        data class SectionListContinuation(
            val contents: List<SectionListRenderer.Content>,
            val continuations: List<Continuation>?,
        )

        @Serializable
        data class MusicPlaylistShelfContinuation(
            val contents: List<MusicShelfRenderer.Content>,
            val continuations: List<Continuation>?,
        )
    }

    @Serializable
    data class Header(
        val musicImmersiveHeaderRenderer: MusicImmersiveHeaderRenderer?,
        val musicDetailHeaderRenderer: MusicDetailHeaderRenderer?,
        val musicEditablePlaylistDetailHeaderRenderer: MusicEditablePlaylistDetailHeaderRenderer?,
        val musicVisualHeaderRenderer: MusicVisualHeaderRenderer?,
        val musicHeaderRenderer: MusicHeaderRenderer?,
    ) {
        @Serializable
        data class MusicImmersiveHeaderRenderer(
            val title: Runs,
            val description: Runs?,
            val thumbnail: ThumbnailRenderer?,
            val playButton: Button?,
            val startRadioButton: Button?,
            val menu: Menu,
        )

        @Serializable
        data class MusicDetailHeaderRenderer(
            val title: Runs,
            val subtitle: Runs,
            val secondSubtitle: Runs,
            val description: Runs?,
            val thumbnail: ThumbnailRenderer,
            val menu: Menu,
        )

        @Serializable
        data class MusicEditablePlaylistDetailHeaderRenderer(
            val header: Header,
        ) {
            @Serializable
            data class Header(
                val musicDetailHeaderRenderer: MusicDetailHeaderRenderer,
            )
        }

        @Serializable
        data class MusicVisualHeaderRenderer(
            val title: Runs,
            val foregroundThumbnail: ThumbnailRenderer,
            val thumbnail: ThumbnailRenderer?,
        )

        @Serializable
        data class MusicHeaderRenderer(
            val title: Runs,
        )
    }

    @Serializable
    data class Microformat(
        val microformatDataRenderer: MicroformatDataRenderer?,
    ) {
        @Serializable
        data class MicroformatDataRenderer(
            val urlCanonical: String?,
        )
    }
}
