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

package jp.nephy.glados.clients.web.routing.meta

import kotlinx.html.*
import kotlinx.io.charsets.Charset

/**
 * Convenience extension.
 * If value is non-null, calls the block.
 */
inline fun <T: Any?> HEAD.ifNotNull(value: T?, block: HEAD.(T) -> Unit) {
    if (value != null) {
        block(value)
    }
}

/**
 * Applies the block.
 */
operator fun <T: PageMetaTemplate> T.invoke(block: T.() -> Unit) {
    apply(block)
}

/**
 * Default page meta template.
 */
open class PageMetaTemplate {
    /**
     * Page charset.
     */
    var charset: Charset = Charsets.UTF_8

    private var ogEnabled = false
    
    /**
     * OpenGraph type.
     */
    val types: MutableSet<OpenGraph.Type> = mutableSetOf()

    /**
     * Page title.
     */
    var title: String? = null

    /**
     * Page description.
     */
    var description: String? = null

    /**
     * Page author.
     */
    var author: String? = null

    /**
     * Page favicon.
     */
    var favicon: String? = null

    /**
     * Page canonical url.
     */
    var canonical: String? = null

    /**
     * Viewport.
     */
    var viewport: String? = null

    /**
     * Robots.
     */
    val robots: MutableSet<RobotsPolicy> = mutableSetOf()

    /**
     * List of disabled [DetectionFormat]
     */
    val disabledDetectionFormats: MutableSet<DetectionFormat> = mutableSetOf()

    /**
     * og:url
     */
    var url: String? = null
        set(value) {
            ogEnabled = true
            field = value
        }

    /**
     * og:site_name
     */
    var siteName: String? = null
        set(value) {
            ogEnabled = true
            field = value
        }
    
    /**
     * og:locale
     */
    var locale: OpenGraph.Locale? = null
        set(value) {
            ogEnabled = true
            field = value
        }
    
    /**
     * og:determiner
     */
    var determiner: OpenGraph.Determiner? = null
        set(value) {
            ogEnabled = true
            field = value
        }
    
    /**
     * List of og:image
     */
    val images: MutableList<OpenGraph.Image> = mutableListOf()

    /**
     * List of og:audio
     */
    val audios: MutableList<OpenGraph.Audio> = mutableListOf()

    /**
     * List of og:video
     */
    val videos: MutableList<OpenGraph.Video> = mutableListOf()

    /**
     * og:song
     */
    var song: OpenGraph.Extended.Music.Song? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.MusicSong
            
            field = value
        }

    /**
     * og:album
     */
    var album: OpenGraph.Extended.Music.Album? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.MusicAlbum
            
            field = value
        }

    /**
     * og:playlist
     */
    var playlist: OpenGraph.Extended.Music.Playlist? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.MusicPlaylist
            
            field = value
        }

    /**
     * og:radio_station
     */
    var radioStation: OpenGraph.Extended.Music.RadioStation? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.MusicRadioStation

            field = value
        }

    /**
     * og:movie
     */
    var movie: OpenGraph.Extended.Video.Movie? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.VideoMovie

            field = value
        }
    
    /**
     * og:episode
     */
    var episode: OpenGraph.Extended.Video.Episode? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.VideoEpisode

            field = value
        }
    
    /**
     * og:tvshow
     */
    var tvShow: OpenGraph.Extended.Video.TVShow? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.VideoTVShow

            field = value
        }
    
    /**
     * og:video:other
     */
    var videoOther: OpenGraph.Extended.Video.Other? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.VideoOther

            field = value
        }
    
    /**
     * og:article
     */
    var article: OpenGraph.Extended.Article? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.Article

            field = value
        }
    
    /**
     * og:book
     */
    var book: OpenGraph.Extended.Book? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.Book

            field = value
        }
    
    /**
     * og:profile
     */
    var profile: OpenGraph.Extended.Profile? = null
        set(value) {
            ogEnabled = true
            types += OpenGraph.Type.MusicRadioStation

            field = value
        }
    
    /**
     * TwitterCard type.
     */
    var twitterCardType: TwitterCard.Type? = null

    /**
     * TwitterCard site account (@xxx).
     */
    var twitterCardSite: String? = null

    /**
     * TwitterCard creator account (@xxx).
     */
    var twitterCardCreator: String? = null

    /**
     * apple-touch-icon
     */
    var appleTouchIcon: String? = null

    /**
     * apple-touch-startup-image
     */
    var appleTouchStartupImage: String? = null

    /**
     *  apple-mobile-web-app-title
     */
    var appleMobileWebAppTitle: String? = null

    /**
     * apple-mobile-web-app-capable
     */
    var appleMobileWebAppCapable: Boolean? = null

    /**
     * apple-mobile-web-app-status-bar-style
     */
    var appleMobileWebAppStatusBarStyle: String? = null

    /**
     * theme-color (#xxxxxx)
     */
    var themeColor: String? = null

    /**
     * Applies to HEAD tag on top.
     */
    open fun HEAD.top() {
        if (ogEnabled) {
            attributes["prefix"] = "og: http://ogp.me/ns# ${types.joinToString(" ") { "${it.content}: ${it.namespaceUri}" }}".trimEnd()
        }

        ifNotNull(charset) {
            charset(it)
        }

        ifNotNull(this@PageMetaTemplate.title) {
            title(it)
        }
        
        if (types.isNotEmpty()) { 
            ogType(*types.toTypedArray())
        }
    }

    /**
     * Applies to HEAD tag on bottom.
     */
    open fun HEAD.bottom() {
        ifNotNull(description) {
            description(it)
        }
        
        ifNotNull(author) {
            author(it)
        }

        ifNotNull(favicon) {
            favicon(it)
        }
        
        ifNotNull(canonical) {
            canonicalUrl(it)
        }
        
        ifNotNull(viewport) {
            viewport(it)
        }
        
        if (robots.isNotEmpty()) {
            robots(*robots.toTypedArray())
        }
        
        if (disabledDetectionFormats.isNotEmpty()) {
            disableFormatDetection(*disabledDetectionFormats.toTypedArray())
        }
        
        ifNotNull(this@PageMetaTemplate.title) {
            ogTitle(it)
        }
        
        ifNotNull(description) {
            ogDescription(it)
        }
        
        ifNotNull(url) {
            ogUrl(it)
        }
        
        ifNotNull(siteName) {
            ogSiteName(it)
        }
        
        ifNotNull(locale) {
            ogLocale(it)
        }
        
        ifNotNull(determiner) {
            ogDeterminer(it)
        }
        
        ogImage(images)
        ogAudio(audios)
        ogVideo(videos)
        
        ifNotNull(song) {
            ogMusicSong(it)
        }
        
        ifNotNull(album) {
            ogMusicAlbum(it)
        }
        
        ifNotNull(playlist) {
            ogMusicPlaylist(it)
        }
        
        ifNotNull(radioStation) {
            ogMusicRadioStation(it)
        }
        
        ifNotNull(movie) {
            ogVideoMovie(it)
        }

        ifNotNull(episode) {
            ogVideoEpisode(it)
        }

        ifNotNull(tvShow) {
            ogVideoTVShow(it)
        }
        
        ifNotNull(videoOther) {
            ogVideoOther(it)
        }
        
        ifNotNull(article) {
            ogArticle(it)
        }
        
        ifNotNull(book) {
            ogBook(it)
        }
        
        ifNotNull(profile) {
            ogProfile(it)
        }
        
        ifNotNull(twitterCardType) {
            twitterCard(it, twitterCardSite, twitterCardCreator)
        }
        
        ifNotNull(appleTouchIcon) {
            appleTouchIcon(it)
        }
        
        ifNotNull(appleTouchStartupImage) {
            appleTouchStartupImage(it)
        }
        
        ifNotNull(appleMobileWebAppTitle) {
            appleMobileWebAppTitle(it)
        }
        
        ifNotNull(appleMobileWebAppCapable) {
            appleMobileWebAppCapable(it)
        }
        
        ifNotNull(appleMobileWebAppStatusBarStyle) {
            appleMobileWebAppStatusBarStyle(it)
        }
        
        ifNotNull(themeColor) {
            themeColor(it)
        }
    }
}
