/*
 * The MIT License (MIT)
 *
 *     Copyright (c) 2017-2019 Nephy Project Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

@file:Suppress("UNUSED")

package jp.nephy.glados.clients.web.routing.meta

import kotlinx.html.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal

@HtmlTagMarker
fun HEAD.ogTitle(title: String) {
    meta(content = title) {
        attributes["property"] = "og:title"
    }
}

@HtmlTagMarker
fun HEAD.ogType(vararg types: OpenGraph.Type) {
    types.forEach {
        meta(content = it.content) {
            attributes["property"] = "og:type"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogImage(images: Collection<OpenGraph.Image>) {
    images.forEach {
        meta(content = it.url) {
            attributes["property"] = "og:image"
        }
        
        if (it.secureUrl != null) {
            meta(content = it.secureUrl) {
                attributes["property"] = "og:image:secure_url"
            }
        }

        if (it.type != null) {
            meta(content = it.type.toString()) {
                attributes["property"] = "og:image:type"
            }
        }

        if (it.width != null) {
            meta(content = it.width.toString()) {
                attributes["property"] = "og:image:width"
            }
        }

        if (it.height != null) {
            meta(content = it.height.toString()) {
                attributes["property"] = "og:image:height"
            }
        }

        if (it.alt != null) {
            meta(content = it.alt) {
                attributes["property"] = "og:image:alt"
            }
        }
    }
}

@HtmlTagMarker
fun HEAD.ogUrl(url: String) {
    meta(content = url) {
        attributes["property"] = "og:url"
    }
}

@HtmlTagMarker
fun HEAD.ogAudio(audios: Collection<OpenGraph.Audio>) {
    audios.forEach {
        meta(content = it.url) {
            attributes["property"] = "og:audio"
        }

        if (it.secureUrl != null) {
            meta(content = it.secureUrl) {
                attributes["property"] = "og:audio:secure_url"
            }
        }

        if (it.type != null) {
            meta(content = it.type.toString()) {
                attributes["property"] = "og:audio:type"
            }
        }
    }
}

@HtmlTagMarker
fun HEAD.ogDescription(description: String) {
    meta(content = description) {
        attributes["property"] = "og:description"
    }
}

@HtmlTagMarker
fun HEAD.ogDeterminer(determiner: OpenGraph.Determiner) {
    meta(content = determiner.content) {
        attributes["property"] = "og:determiner"
    }
}

@HtmlTagMarker
fun HEAD.ogLocale(locale: OpenGraph.Locale) {
    meta(content = locale.locale) {
        attributes["property"] = "og:locale"
    }

    locale.alternate.forEach {
        meta(content = it) {
            attributes["property"] = "og:locale:alternate"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogSiteName(name: String) {
    meta(content = name) {
        attributes["property"] = "og:site_name"
    }
}

@HtmlTagMarker
fun HEAD.ogVideo(videos: Collection<OpenGraph.Video>) {
    videos.forEach {
        meta(content = it.url) {
            attributes["property"] = "og:video"
        }

        if (it.secureUrl != null) {
            meta(content = it.secureUrl) {
                attributes["property"] = "og:video:secure_url"
            }
        }

        if (it.type != null) {
            meta(content = it.type.toString()) {
                attributes["property"] = "og:video:type"
            }
        }

        if (it.width != null) {
            meta(content = it.width.toString()) {
                attributes["property"] = "og:video:width"
            }
        }

        if (it.height != null) {
            meta(content = it.height.toString()) {
                attributes["property"] = "og:video:height"
            }
        }
    }
}

@HtmlTagMarker
fun HEAD.ogMusicSong(song: OpenGraph.Extended.Music.Song) {
    if (song.duration != null) {
        meta(content = song.duration.toString()) {
            attributes["property"] = "music:duration"
        }
    }
    
    song.album.forEach {
        meta(content = it.content) {
            attributes["property"] = "music:album"
        }
        
        if (it.disc != null) {
            meta(content = it.disc.toString()) {
                attributes["property"] = "music:album:disc"
            }
        }

        if (it.track != null) {
            meta(content = it.track.toString()) {
                attributes["property"] = "music:album:track"
            }
        }
    }

    song.musician.forEach {
        meta(content = it) {
            attributes["property"] = "music:musician"
        }
    }
}

private fun Temporal.toISO8601Format(): String {
    return DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("GMT")).format(this)
}

@HtmlTagMarker
fun HEAD.ogMusicAlbum(album: OpenGraph.Extended.Music.Album) {
    if (album.song != null) {
        meta(content = album.song.content) {
            attributes["property"] = "music:song"
        }

        if (album.song.disc != null) {
            meta(content = album.song.disc.toString()) {
                attributes["property"] = "music:song:disc"
            }
        }

        if (album.song.track != null) {
            meta(content = album.song.track.toString()) {
                attributes["property"] = "music:song:track"
            }
        }
    }

    if (album.musician != null) {
        meta(content = album.musician) {
            attributes["property"] = "music:musician"
        }
    }

    if (album.releaseDate != null) {
        meta(content = album.releaseDate.toISO8601Format()) {
            attributes["property"] = "music:release_date"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogMusicPlaylist(playlist: OpenGraph.Extended.Music.Playlist) {
    if (playlist.song != null) {
        meta(content = playlist.song.content) {
            attributes["property"] = "music:song"
        }

        if (playlist.song.disc != null) {
            meta(content = playlist.song.disc.toString()) {
                attributes["property"] = "music:song:disc"
            }
        }

        if (playlist.song.track != null) {
            meta(content = playlist.song.track.toString()) {
                attributes["property"] = "music:song:track"
            }
        }
    }

    if (playlist.creator != null) {
        meta(content = playlist.creator) {
            attributes["property"] = "music:creator"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogMusicRadioStation(radioStation: OpenGraph.Extended.Music.RadioStation) {
    if (radioStation.creator != null) {
        meta(content = radioStation.creator) {
            attributes["property"] = "music:creator"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogVideoMovie(movie: OpenGraph.Extended.Video.Movie) {
    movie.actor.forEach {
        meta(content = it.content) {
            attributes["property"] = "video:actor"
        }
        
        if (it.role != null) {
            meta(content = it.role) {
                attributes["property"] = "video:actor:role"
            }
        }
    }
    
    movie.director.forEach { 
        meta(content = it) {
            attributes["property"] = "video:director"
        }
    }

    movie.writer.forEach {
        meta(content = it) {
            attributes["property"] = "video:writer"
        }
    }
    
    if (movie.duration != null) {
        meta(content = movie.duration.toString()) {
            attributes["property"] = "video:duration"
        }
    }

    if (movie.releaseDate != null) {
        meta(content = movie.releaseDate.toISO8601Format()) {
            attributes["property"] = "video:release_date"
        }
    }
    
    movie.tag.forEach {
        meta(content = it) {
            attributes["property"] = "video:tag"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogVideoEpisode(episode: OpenGraph.Extended.Video.Episode) {
    episode.actor.forEach {
        meta(content = it.content) {
            attributes["property"] = "video:actor"
        }

        if (it.role != null) {
            meta(content = it.role) {
                attributes["property"] = "video:actor:role"
            }
        }
    }

    episode.director.forEach {
        meta(content = it) {
            attributes["property"] = "video:director"
        }
    }

    episode.writer.forEach {
        meta(content = it) {
            attributes["property"] = "video:writer"
        }
    }

    if (episode.duration != null) {
        meta(content = episode.duration.toString()) {
            attributes["property"] = "video:duration"
        }
    }

    if (episode.releaseDate != null) {
        meta(content = episode.releaseDate.toISO8601Format()) {
            attributes["property"] = "video:release_date"
        }
    }

    episode.tag.forEach {
        meta(content = it) {
            attributes["property"] = "video:tag"
        }
    }
    
    if (episode.series != null) {
        meta(content = episode.series) {
            attributes["property"] = "video:series"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogVideoTVShow(tvShow: OpenGraph.Extended.Video.TVShow) {
    tvShow.actor.forEach {
        meta(content = it.content) {
            attributes["property"] = "video:actor"
        }

        if (it.role != null) {
            meta(content = it.role) {
                attributes["property"] = "video:actor:role"
            }
        }
    }

    tvShow.director.forEach {
        meta(content = it) {
            attributes["property"] = "video:director"
        }
    }

    tvShow.writer.forEach {
        meta(content = it) {
            attributes["property"] = "video:writer"
        }
    }

    if (tvShow.duration != null) {
        meta(content = tvShow.duration.toString()) {
            attributes["property"] = "video:duration"
        }
    }

    if (tvShow.releaseDate != null) {
        meta(content = tvShow.releaseDate.toISO8601Format()) {
            attributes["property"] = "video:release_date"
        }
    }

    tvShow.tag.forEach {
        meta(content = it) {
            attributes["property"] = "video:tag"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogVideoOther(other: OpenGraph.Extended.Video.Other) {
    other.actor.forEach {
        meta(content = it.content) {
            attributes["property"] = "video:actor"
        }

        if (it.role != null) {
            meta(content = it.role) {
                attributes["property"] = "video:actor:role"
            }
        }
    }

    other.director.forEach {
        meta(content = it) {
            attributes["property"] = "video:director"
        }
    }

    other.writer.forEach {
        meta(content = it) {
            attributes["property"] = "video:writer"
        }
    }

    if (other.duration != null) {
        meta(content = other.duration.toString()) {
            attributes["property"] = "video:duration"
        }
    }

    if (other.releaseDate != null) {
        meta(content = other.releaseDate.toISO8601Format()) {
            attributes["property"] = "video:release_date"
        }
    }

    other.tag.forEach {
        meta(content = it) {
            attributes["property"] = "video:tag"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogArticle(article: OpenGraph.Extended.Article) {
    if (article.publishedTime != null) {
        meta(content = article.publishedTime.toISO8601Format()) {
            attributes["property"] = "article:published_time"
        }
    }

    if (article.modifiedTime != null) {
        meta(content = article.modifiedTime.toISO8601Format()) {
            attributes["property"] = "article:modified_time"
        }
    }
    
    if (article.expirationTime != null) {
        meta(content = article.expirationTime.toISO8601Format()) {
            attributes["property"] = "article:expiration_time"
        }
    }
    
    article.author.forEach {
        meta(content = it) {
            attributes["property"] = "article:author"
        }
    }
    
    if (article.section != null) {
        meta(content = article.section) {
            attributes["property"] = "article:section"
        }
    }

    article.tag.forEach {
        meta(content = it) {
            attributes["property"] = "article:tag"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogBook(book: OpenGraph.Extended.Book) {
    book.author.forEach {
        meta(content = it) {
            attributes["property"] = "book:author"
        }
    }
    
    if (book.isbn != null) {
        meta(content = book.isbn) {
            attributes["property"] = "book:isbn"
        }
    }

    if (book.releaseDate != null) {
        meta(content = book.releaseDate.toISO8601Format()) {
            attributes["property"] = "book:release_date"
        }
    }

    book.tag.forEach {
        meta(content = it) {
            attributes["property"] = "book:tag"
        }
    }
}

@HtmlTagMarker
fun HEAD.ogProfile(profile: OpenGraph.Extended.Profile) {
    if (profile.firstName != null) {
        meta(content = profile.firstName) {
            attributes["property"] = "profile:first_name"
        }
    }

    if (profile.lastName != null) {
        meta(content = profile.lastName) {
            attributes["property"] = "profile:last_name"
        }
    }

    if (profile.username != null) {
        meta(content = profile.username) {
            attributes["property"] = "profile:username"
        }
    }

    if (profile.gender != null) {
        meta(content = profile.gender.content) {
            attributes["property"] = "profile:gender"
        }
    }
}
