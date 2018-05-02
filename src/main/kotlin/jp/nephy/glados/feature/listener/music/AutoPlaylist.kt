package jp.nephy.glados.feature.listener.music

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import jp.nephy.glados.component.audio.music.PlayerLoadResultHandler
import jp.nephy.glados.component.audio.music.TrackType
import jp.nephy.glados.component.audio.music.groupIdSetter
import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.logger
import net.dv8tion.jda.core.events.ReadyEvent
import java.util.*


class AutoPlaylist: ListenerFeature() {
    private val playlist = arrayOf(
            "https://www.youtube.com/playlist?list=PLYlndC1jl8s2G7DjwH3aMBDbG4JNxAwa4"
    )

    override fun onReady(event: ReadyEvent) {
        event.jda.guilds.forEach {
            val config = bot.config.getGuildConfig(it)
            if (config.voiceChannel.general == null) {
                return@forEach
            }

            val guildPlayer = bot.playerManager.getGuildPlayer(it)
            logger.info { "ボイスチャンネル: ${guildPlayer.voiceChannel.name} (${it.name}) に接続しました." }
            if (! config.option.useAutoPlaylist) {
                return@forEach
            }

            val groupId = Date().time
            playlist.forEach {
                guildPlayer.loadTrack(it, TrackType.AutoPlaylist, object: PlayerLoadResultHandler {
                    override fun onLoadTrack(track: AudioTrack) {
                        track.groupIdSetter = groupId
                        guildPlayer.controls.add(track)
                    }

                    override fun onLoadPlaylist(playlist: AudioPlaylist) {
                        playlist.tracks.shuffle()
                        playlist.tracks.forEach {
                            it.groupIdSetter = groupId
                        }
                        guildPlayer.controls.addAll(playlist.tracks)
                    }
                })
            }
        }

        logger.info { "オートプレイリストの読み込みが完了しました." }
    }
}
