package com.maloy.innertube.pages

import com.maloy.innertube.models.Album
import com.maloy.innertube.models.AlbumItem
import com.maloy.innertube.models.Artist
import com.maloy.innertube.models.MusicResponsiveListItemRenderer
import com.maloy.innertube.models.SongItem
import com.maloy.innertube.models.oddElements
import com.maloy.innertube.utils.parseTime

data class AlbumPage(
    val album: AlbumItem,
    val songs: List<SongItem>,
) {
    companion object {
        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): SongItem? {
            return SongItem(
                id = renderer.playlistItemData?.videoId ?: return null,
                title = renderer.flexColumns.firstOrNull()
                    ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs
                    ?.firstOrNull()?.text ?: return null,
                artists = renderer.flexColumns.getOrNull(1)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.oddElements()?.map {
                    Artist(
                        name = it.text,
                        id = it.navigationEndpoint?.browseEndpoint?.browseId
                    )
                } ?: return null,
                album = renderer.flexColumns.getOrNull(2)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()?.let {
                    Album(
                        name = it.text,
                        id = it.navigationEndpoint?.browseEndpoint?.browseId!!
                    )
                } ?: return null,
                duration = renderer.fixedColumns?.firstOrNull()
                    ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()
                    ?.text?.parseTime() ?: return null,
                thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                explicit = renderer.badges?.find {
                    it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                } != null
            )
        }
    }
}