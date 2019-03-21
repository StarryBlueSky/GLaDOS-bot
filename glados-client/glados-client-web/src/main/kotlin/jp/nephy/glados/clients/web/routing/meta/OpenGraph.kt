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

import java.time.temporal.Temporal

/**
 * The Open Graph protocol enables any web page to become a rich object in a social graph. For instance, this is used on Facebook to allow any web page to have the same functionality as any other object on Facebook.
 * 
 * While many different technologies and schemas exist and could be combined together, there isn't a single technology which provides enough information to richly represent any web page within the social graph. The Open Graph protocol builds on these existing technologies and gives developers one thing to implement. Developer simplicity is a key goal of the Open Graph protocol which has informed many of the technical design decisions.
 */
object OpenGraph {
    /**
     * # Object Types
     * In order for your object to be represented within the graph, you need to specify its type. This is done using the og:type property:
     * ```html
     * <meta property="og:type" content="website" />
     * ```
     * When the community agrees on the schema for a type, it is added to the list of global types. All other objects in the type system are CURIEs of the form
     * ```
     * <head prefix="my_namespace: http://example.com/ns#">
     * <meta property="og:type" content="my_namespace:my_type" />
     * ```
     * The global types are grouped into verticals. Each vertical has its own namespace. The og:type values for a namespace are always prefixed with the namespace and then a period. This is to reduce confusion with user-defined namespaced types which always have colons in them.
     */
    enum class Type(
        /**
         * Meta property content.
         */
        val content: String,

        /**
         * Namespace URI.
         */
        val namespaceUri: String
    ) {
        /**
         * Namespace URI: http://ogp.me/ns/website#
         * 
         * No additional properties other than the basic ones. Any non-marked up webpage should be treated as og:type website.
         */
        Website("website", "http://ogp.me/ns/website#"),

        /**
         * Namespace URI: http://ogp.me/ns/article#
         * 
         * - article:published_time - datetime - When the article was first published.
         * - article:modified_time - datetime - When the article was last changed.
         * - article:expiration_time - datetime - When the article is out of date after.
         * - article:author - profile array - Writers of the article.
         * - article:section - string - A high-level section name. E.g. Technology
         * - article:tag - string array - Tag words associated with this article.
         */
        Article("article", "http://ogp.me/ns/article#"),

        /**
         * Namespace URI: http://ogp.me/ns/book#
         * 
         * - book:author - profile array - Who wrote this book.
         * - book:isbn - string - The ISBN
         * - book:release_date - datetime - The date the book was released.
         * - book:tag - string array - Tag words associated with this book.
         */
        Book("book", "http://ogp.me/ns/book#"),

        /**
         * Namespace URI: http://ogp.me/ns/profile#
         * 
         * - profile:first_name - string - A name normally given to an individual by a parent or self-chosen.
         * - profile:last_name - string - A name inherited from a family or marriage and by which the individual is commonly known.
         * - profile:username - string - A short unique string to identify them.
         * - profile:gender - enum(male, female) - Their gender.
         */
        Profile("profile", "http://ogp.me/ns/profile#"),

        /**
         * Namespace URI: http://ogp.me/ns/music#
         * 
         * - music:duration - integer >=1 - The song's length in seconds.
         * - music:album - music.album array - The album this song is from.
         * - music:album:disc - integer >=1 - Which disc of the album this song is on.
         * - music:album:track - integer >=1 - Which track this song is.
         * - music:musician - profile array - The musician that made this song.
         */
        MusicSong("music.song", "http://ogp.me/ns/music#"),

        /**
         * Namespace URI: http://ogp.me/ns/music#
         * 
         * - music:song - music.song - The song on this album.
         * - music:song:disc - integer >=1 - The same as music:album:disc but in reverse.
         * - music:song:track - integer >=1 - The same as music:album:track but in reverse.
         * - music:musician - profile - The musician that made this song.
         * - music:release_date - datetime - The date the album was released.
         */
        MusicAlbum("music.album", "http://ogp.me/ns/music#"),

        /**
         * Namespace URI: http://ogp.me/ns/music#
         * 
         * - music:song - Identical to the ones on music.album
         * - music:song:disc
         * - music:song:track
         * - music:creator - profile - The creator of this playlist.
         */
        MusicPlaylist("music.playlist", "http://ogp.me/ns/music#"),

        /**
         * Namespace URI: http://ogp.me/ns/music#
         * 
         * - music:creator - profile - The creator of this station.
         */
        MusicRadioStation("music.radio_station", "http://ogp.me/ns/music#"),

        /**
         * Namespace URI: http://ogp.me/ns/video#
         * 
         * - video:actor - profile array - Actors in the movie.
         * - video:actor:role - string - The role they played.
         * - video:director - profile array - Directors of the movie.
         * - video:writer - profile array - Writers of the movie.
         * - video:duration - integer >=1 - The movie's length in seconds.
         * - video:release_date - datetime - The date the movie was released.
         * - video:tag - string array - Tag words associated with this movie.
         */
        VideoMovie("video.movie", "http://ogp.me/ns/video#"),

        /**
         * Namespace URI: http://ogp.me/ns/video#
         * 
         * - video:actor - Identical to video.movie
         * - video:actor:role
         * - video:director
         * - video:writer
         * - video:duration
         * - video:release_date
         * - video:tag
         * - video:series - video.tv_show - Which series this episode belongs to.
         */
        VideoEpisode("video.episode", "http://ogp.me/ns/video#"),

        /**
         * Namespace URI: http://ogp.me/ns/video#
         * 
         * A multi-episode TV show. The metadata is identical to video.movie.
         */
        VideoTVShow("video.tv_show", "http://ogp.me/ns/video#"),

        /**
         * Namespace URI: http://ogp.me/ns/video#
         * 
         * A video that doesn't belong in any other category. The metadata is identical to video.movie.
         */
        VideoOther("video.other", "http://ogp.me/ns/video#")
    }

    /**
     * The locale.
     */
    data class Locale(
        /**
         * The locale these tags are marked up in. Of the format language_TERRITORY. Default is en_US.
         */
        val locale: String,

        /**
         * An array of other locales this page is available in.
         */
        val alternate: List<String> = emptyList()
    )

    /**
     * The image.
     */
    data class Image(
        /**
         * Identical to og:image.
         */
        val url: String,

        /**
         * An alternate url to use if the webpage requires HTTPS.
         */
        val secureUrl: String? = null,

        /**
         * A MIME type for this image.
         */
        val type: String? = null,

        /**
         * The number of pixels wide.
         */
        val width: Int? = null,

        /**
         * The number of pixels high.
         */
        val height: Int? = null,

        /**
         * A description of what is in the image (not a caption). If the page specifies an og:image it should specify og:image:alt.
         */
        val alt: String? = null
    )

    /**
     * The video.
     */
    data class Video(
        /**
         * A URL to a video file that complements this object.
         */
        val url: String,

        /**
         * An alternate url to use if the webpage requires HTTPS.
         */
        val secureUrl: String? = null,

        /**
         * A MIME type for this image.
         */
        val type: String? = null,

        /**
         * The number of pixels wide.
         */
        val width: Int? = null,

        /**
         * The number of pixels high.
         */
        val height: Int? = null
    )

    /**
     * The audio.
     */
    data class Audio(
        /**
         * A URL to a video file that complements this object.
         */
        val url: String,

        /**
         * An alternate url to use if the webpage requires HTTPS.
         */
        val secureUrl: String? = null,

        /**
         * A MIME type for this image.
         */
        val type: String? = null
    )

    /**
     * The extend structure.
     */
    object Extended {
        /**
         * The music.
         * 
         * Namespace URI: http://ogp.me/ns/music#
         */
        object Music {
            /**
             * music.song
             */
            data class Song(
                /**
                 * The song's length in seconds.
                 */
                val duration: Int? = null,

                /**
                 * The album this song is from.
                 */
                val album: List<Album> = emptyList(),

                /**
                 * The musician that made this song.
                 */
                val musician: List<Profile> = emptyList(),

                /**
                 * Which disc of the album this song is on.
                 */
                val disc: Int? = null,

                /**
                 * Which track this song is.
                 */
                val track: Int? = null
            )

            /**
             * music.album
             */
            data class Album(
                /**
                 * The song on this album.
                 */
                val song: Song? = null,

                /**
                 * The musician that made this song.
                 */
                val musician: Profile? = null,

                /**
                 * The date the album was released.
                 */
                val releaseDate: Temporal? = null,

                /**
                 * The same as music:album:disc but in reverse.
                 */
                val disc: Int? = null,

                /**
                 * The same as music:album:track but in reverse.
                 */
                val track: Int? = null
            )

            /**
             * music.playlist
             */
            data class Playlist(
                /**
                 * Identical to the ones on music.album
                 */
                val song: Song? = null,

                /**
                 * The creator of this playlist.
                 */
                val creator: Profile? = null,

                /**
                 * The same as music:album:disc but in reverse.
                 */
                val disc: Int? = null,

                /**
                 * The same as music:album:track but in reverse.
                 */
                val track: Int? = null
            )

            /**
             * music.radio_station
             */
            data class RadioStation(
                /**
                 * The creator of this station.
                 */
                val creator: Profile? = null
            )
        }

        /**
         * The video.
         * 
         * Namespace URI: http://ogp.me/ns/video#
         */
        object Video {
            /**
             * The actor.
             */
            data class Actor(
                /**
                 * A name normally given to an individual by a parent or self-chosen.
                 */
                val firstName: String? = null,

                /**
                 * A name inherited from a family or marriage and by which the individual is commonly known.
                 */
                val lastName: String? = null,

                /**
                 * A short unique string to identify them.
                 */
                val username: String? = null,

                /**
                 * Their gender.
                 */
                val gender: Gender? = null,

                /**
                 * The role they played.
                 */
                val role: String? = null
            )

            /**
             * video.movie
             */
            data class Movie(
                /**
                 * Actors in the movie.
                 */
                val actor: List<Actor> = emptyList(),

                /**
                 * Directors of the movie.
                 */
                val director: List<Profile> = emptyList(),

                /**
                 * Writers of the movie.
                 */
                val writer: List<Profile> = emptyList(),

                /**
                 * The movie's length in seconds.
                 */
                val duration: Int? = null,

                /**
                 * The date the movie was released.
                 */
                val releaseDate: Temporal? = null,

                /**
                 * Tag words associated with this movie.
                 */
                val tag: List<String> = emptyList()
            )

            /**
             * video.episode
             */
            data class Episode(
                /**
                 * Identical to video.movie.
                 */
                val actor: List<Actor> = emptyList(),

                /**
                 * Directors of the movie.
                 */
                val director: List<Profile> = emptyList(),

                /**
                 * Writers of the movie.
                 */
                val writer: List<Profile> = emptyList(),

                /**
                 * The movie's length in seconds.
                 */
                val duration: Int? = null,

                /**
                 * The date the movie was released.
                 */
                val releaseDate: Temporal? = null,

                /**
                 * Tag words associated with this movie.
                 */
                val tag: List<String> = emptyList(),

                /**
                 * Which series this episode belongs to.
                 */
                val series: TVShow? = null
            )

            /**
             * video.tv_show
             */
            data class TVShow(
                /**
                 * Actors in the movie.
                 */
                val actor: List<Actor> = emptyList(),

                /**
                 * Directors of the movie.
                 */
                val director: List<Profile> = emptyList(),

                /**
                 * Writers of the movie.
                 */
                val writer: List<Profile> = emptyList(),

                /**
                 * The movie's length in seconds.
                 */
                val duration: Int? = null,

                /**
                 * The date the movie was released.
                 */
                val releaseDate: Temporal? = null,

                /**
                 * Tag words associated with this movie.
                 */
                val tag: List<String> = emptyList()
            )

            /**
             * video.other
             */
            data class Other(
                /**
                 * Actors in the movie.
                 */
                val actor: List<Actor> = emptyList(),

                /**
                 * Directors of the movie.
                 */
                val director: List<Profile> = emptyList(),

                /**
                 * Writers of the movie.
                 */
                val writer: List<Profile> = emptyList(),

                /**
                 * The movie's length in seconds.
                 */
                val duration: Int? = null,

                /**
                 * The date the movie was released.
                 */
                val releaseDate: Temporal? = null,

                /**
                 * Tag words associated with this movie.
                 */
                val tag: List<String> = emptyList()
            )
        }

        /**
         * The article.
         * 
         * Namespace URI: http://ogp.me/ns/article#
         */
        data class Article(
            /**
             * When the article was first published.
             */
            val publishedTime: Temporal? = null,

            /**
             * When the article was last changed.
             */
            val modifiedTime: Temporal? = null,

            /**
             * When the article is out of date after.
             */
            val expirationTime: Temporal? = null,

            /**
             * Writers of the article.
             */
            val author: List<Profile> = emptyList(),

            /**
             * A high-level section name. E.g. Technology
             */
            val section: String? = null,

            /**
             * Tag words associated with this article.
             */
            val tag: List<String> = emptyList()
        )

        /**
         * The book.
         * 
         * Namespace URI: http://ogp.me/ns/book#
         */
        data class Book(
            /**
             * Who wrote this book.
             */
            val author: List<Profile> = emptyList(),

            /**
             * The ISBN.
             */
            val isbn: String? = null,

            /**
             * The date the book was released.
             */
            val releaseDate: Temporal? = null,

            /**
             * Tag words associated with this book.
             */
            val tag: List<String> = emptyList()
        )

        /**
         * The profile.
         * 
         * Namespace URI: http://ogp.me/ns/profile#
         */
        data class Profile(
            /**
             * A name normally given to an individual by a parent or self-chosen.
             */
            val firstName: String? = null,

            /**
             * A name inherited from a family or marriage and by which the individual is commonly known.
             */
            val lastName: String? = null,

            /**
             * A short unique string to identify them.
             */
            val username: String? = null,

            /**
             * Their gender.
             */
            val gender: Gender? = null
        )
    }

    /**
     * enum(male, female)
     */
    enum class Gender(
        /**
         * Meta property content.
         */
        val content: String
    ) {
        /**
         * Male.
         */
        Male("male"),

        /**
         * Female.
         */
        Female("female")
    }

    /**
     * The word that appears before this object's title in a sentence. An enum of (a, an, the, "", auto).
     * If auto is chosen, the consumer of your data should chose between "a" or "an". Default is "" (blank).
     */
    enum class Determiner(
        /**
         * Meta property content.
         */
        val content: String
    ) {
        /**
         * Default "" (blank).
         */
        Default(""),

        /**
         * "a".
         */
        A("a"),

        /**
         * "an".
         */
        An("an"),

        /**
         * "the".
         */
        The("the"),

        /**
         * auto.
         */
        Auto("auto")
    }
}
