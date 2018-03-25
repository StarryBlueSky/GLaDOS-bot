package jp.nephy.glados.feature.listener.music

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.audio.music.PlayerLoadResultHandler
import jp.nephy.glados.component.audio.music.TrackType
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.ReadyEvent


class AutoPlaylist(bot: GLaDOS): ListenerFeature(bot) {
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
            bot.logger.info { "ボイスチャンネル: ${guildPlayer.voiceChannel.name} (${it.name}) に接続しました." }
            if (! config.option.useAutoPlaylist) {
                return@forEach
            }

            playlist.forEach {
                guildPlayer.loadTrack(it, TrackType.AutoPlaylist, object: PlayerLoadResultHandler {
                    override fun onLoadTrack(track: AudioTrack) {
                        guildPlayer.controls.add(track)
                    }

                    override fun onLoadPlaylist(playlist: AudioPlaylist) {
                        playlist.tracks.shuffle()
                        guildPlayer.controls.addAll(playlist.tracks)
                    }
                })
            }
        }

        bot.logger.info { "オートプレイリストの読み込みが完了しました." }
    }
}
