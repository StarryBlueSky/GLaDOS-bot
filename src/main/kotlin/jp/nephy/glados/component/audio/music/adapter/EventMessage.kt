package jp.nephy.glados.component.audio.music.adapter

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.audio.music.*
import jp.nephy.glados.component.audio.music.PlayerEmoji
import jp.nephy.glados.component.helper.*
import jp.nephy.glados.logger
import net.dv8tion.jda.core.exceptions.ErrorResponseException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class EventMessage(private val guildPlayer: GuildPlayer): AudioEventAdapter() {
    private val bot = GLaDOS.instance

    private fun buildEmbed(builder: EmbedBuilder, track: AudioTrack): EmbedBuilder {
        val info = track.youtubedl.info
        val soundCloud = track.soundCloudCache
        val nico = track.nicoCache
        val nicoRanking = track.nicoRankingCache
        return builder.apply {
            title(track.info.effectiveTitle, track.info.uri)
            descriptionBuilder {
                appendln("by ${track.info.author}\n")
                if (soundCloud != null) {
                    appendln("${soundCloud.track.description.take(150)}...")
                    if (soundCloud.track.labelName != null) {
                        append("Released by:\n    ${soundCloud.track.labelName}")
                    }
                } else if (nico != null) {
                    appendln("登録タグ: ${nico.tags.split(" ").joinToString(" ") { "__**$it**__" }}")
                    appendln("再生数: ${nico.viewCounter} / コメント数: ${nico.commentCounter} / マイリスト数: ${nico.mylistCounter}\n")
                    append("${nico.description.replace("<.+?>".toRegex(), "").take(150)}...")
                } else if (info?.description != null) {
                    append("${info.description!!.replace("<.+?>".toRegex(), "").take(150)}...")
                }
            }
            footer("${track.position.toMilliSecondString()} / ${track.duration.toMilliSecondString()} | ボリューム: ${guildPlayer.controls.volume}%")

            when {
                info?.thumbnailUrl != null -> thumbnail(info.thumbnailUrl!!)
                soundCloud?.track?.artworkUrl != null -> thumbnail(soundCloud.track.artworkUrl!!)
            }

            when (track.type) {
                TrackType.AutoPlaylist -> {
                    author("♪ Now Playing (オートプレイリスト)")
                    color(Color.YouTube)
                }
                TrackType.SoundCloud -> {
                    author("♪ Now Playing (SoundCloud #${soundCloud?.track?.genre} を自動再生中)", "https://soundcloud.com", "https://a-v2.sndcdn.com/assets/images/sc-icons/favicon-2cadd14b.ico")
                    color(Color.SoundCloud)
                }
                TrackType.NicoRanking -> {
                    author("♪ Now Playing (ニコニコ動画 ${nicoRanking?.rankingName}を自動再生中)", "http://www.nicovideo.jp", "http://nicovideo.cdn.nimg.jp/web/img/favicon.ico")
                    title(nicoRanking?.title ?: track.info.effectiveTitle, nicoRanking?.link ?: track.info.uri)
                    color(Color.Niconico)
                }
                TrackType.UserRequest -> when {
                    PlayableVideoURL.SoundCloud.match(track.info.uri) -> {
                        author("♪ Now Playing (SoundCloud)", "https://soundcloud.com", "https://a-v2.sndcdn.com/assets/images/sc-icons/favicon-2cadd14b.ico")
                        color(Color.SoundCloud)
                    }
                    PlayableVideoURL.Niconico.match(track.info.uri) -> {
                        author("♪ Now Playing (ニコニコ動画)", "http://www.nicovideo.jp", "http://nicovideo.cdn.nimg.jp/web/img/favicon.ico")
                        color(Color.Niconico)
                    }
                    PlayableVideoURL.Twitch.match(track.info.uri) -> {
                        author("♪ Now Playing (Twitch)", "https://www.twitch.tv", "https://static.twitchcdn.net/assets/favicon-75270f9df2b07174c23ce844a03d84af.ico")
                        color(Color.Twitch)
                    }
                    PlayableVideoURL.YouTube.match(track.info.uri) -> {
                        author("♪ Now Playing (YouTube)", "https://www.youtube.com", "https://s.ytimg.com/yts/img/favicon_48-vflVjB_Qk.png")
                        color(Color.YouTube)
                    }
                    else -> {
                        author("♪ Now Playing")
                        color(Color.Plain)
                    }
                }
            }

            timestamp()
        }
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        logger.info { "[${track.sourceManager.sourceName}] 現在再生中の曲は \"${track.info.effectiveTitle}\" by ${track.info.author} (${track.info.length.toMilliSecondString()}) です." }

        if (guildPlayer.config.textChannel.bot == null) {
            return
        }

        bot.jda.getTextChannelById(guildPlayer.config.textChannel.bot).embedMessage { buildEmbed(this, track) }.queue {
            thread {
                PlayerEmoji.values().forEach { e ->
                    it.addReaction(e.emoji).queue({}, {})
                }

                var isDeleted = false
                while (track.state == AudioTrackState.LOADING || track.state == AudioTrackState.SEEKING || track.state == AudioTrackState.PLAYING) {
                    if (isDeleted) {
                        return@thread
                    }

                    Thread.sleep(bot.parameter.nowPlayingUpdateMs)
                    it.editMessage(buildEmbed(EmbedBuilder(), track).build()).queue({}, {
                        if (it is ErrorResponseException && it.errorCode == 10008) {
                            isDeleted = true
                        }
                    })
                }

                it.delete().queue({}, {})
            }
        }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (! endReason.mayStartNext) {
            logger.info { "[${track.sourceManager.sourceName}] \"${track.info.effectiveTitle}\" by ${track.info.author} (${track.info.length.toMilliSecondString()}) の再生が停止しました. (${endReason.name})" }
        }
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {
        logger.info { "\"${track.info?.title}\" by ${track.info?.author} (${track.info?.length.toMilliSecondString()}) の再生がスタックしました. (閾値: ${thresholdMs.toMilliSecondString()})" }
    }

    override fun onPlayerPause(player: AudioPlayer) {
        logger.info { "プレイヤー \"$player\" が一時停止しています." }
    }

    override fun onPlayerResume(player: AudioPlayer) {
        logger.info { "プレイヤー \"$player\" が再開しました." }
    }

    override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
        logger.error(exception) { "トラック \"${track.info.effectiveTitle}\" の再生中に例外が発生しました。" }
        if (guildPlayer.config.textChannel.bot == null) {
            return
        }

        bot.jda.getTextChannelById(guildPlayer.config.textChannel.bot).embedMessage {
            author("例外レポート")
            title("\"${track.info.effectiveTitle}\" の再生中に例外が発生しました。")
            description { "${exception.javaClass.canonicalName}: ${exception.localizedMessage}" }
            color(Color.Bad)
            timestamp()
        }.deleteQueue(60, TimeUnit.SECONDS)
    }
}
