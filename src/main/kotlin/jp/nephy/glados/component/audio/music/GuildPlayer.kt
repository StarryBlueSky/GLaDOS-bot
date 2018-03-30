package jp.nephy.glados.component.audio.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.nico.NicoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.audio.AudioRecorder
import jp.nephy.glados.component.audio.ConnectionListenerImpl
import jp.nephy.glados.component.audio.music.adapter.EventMessage
import jp.nephy.glados.component.audio.music.adapter.TrackControls
import jp.nephy.glados.component.helper.sumBy
import jp.nephy.glados.component.helper.toMilliSecondString
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel


class GuildPlayer(private val bot: GLaDOS, val guild: Guild) {
    private val playerManager = DefaultAudioPlayerManager()
    private val player = playerManager.createPlayer()!!.apply {
        volume = bot.parameter.defaultPlayerVolume
    }

    val config = bot.config.getGuildConfig(guild)
    val connectionListener = ConnectionListenerImpl(bot)
    val sendingHandler = AudioSendHandlerImpl(player)
    val receivingHandler = AudioRecorder(bot, this)
    val controls = TrackControls(bot, this, player)

    init {
        if (config.voiceChannel.general == null) {
            throw IllegalStateException("接続するボイスチャンネルが未定義です.")
        }

        playerManager.registerSourceManager(NicoAudioSourceManager(bot.secret.niconicoLoginEmail, bot.secret.niconicoLoginPassword))
        AudioSourceManagers.registerRemoteSources(playerManager)
        // AudioSourceManagers.registerLocalSource(playerManager)

        player.addListener(controls)
        player.addListener(EventMessage(bot, this))
    }

    fun searchTrack(query: String, priority: SearchPriority, limit: Int = 20, handler: PlayerSearchResultHandler) {
        if (priority == SearchPriority.Niconico || priority == SearchPriority.Undefined) {
            val nicoResult = bot.apiClient.niconico.search(query, limit = limit)
            if (nicoResult.data.isNotEmpty()) {
                return handler.onFoundNiconicoResult(nicoResult)
            }
        }

        if (priority == SearchPriority.YouTube || priority == SearchPriority.Undefined) {
            val youtubeResult = bot.apiClient.youtube.search(query, limit)
            if (youtubeResult.isNotEmpty()) {
                return handler.onFoundYouTubeResult(youtubeResult)
            }
        }

        handler.onNoResult()
    }

    fun loadTrack(identifier: String, trackType: TrackType, handler: PlayerLoadResultHandler) {
        playerManager.loadItemOrdered(this, identifier, object: AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                track.typeSetter = trackType

                handler.onLoadTrack(track)
                bot.logger.info { "[${track.sourceManager.javaClass.simpleName}] トラック \"${track.info.effectiveTitle}\" by ${track.info.author} (${track.duration.toMilliSecondString()}) をキューに追加しました." }
            }
            override fun playlistLoaded(playlist: AudioPlaylist) {
                if (playlist.tracks.isEmpty()) {
                    return noMatches()
                }

                playlist.tracks.forEach {
                    it.typeSetter = trackType
                }

                handler.onLoadPlaylist(playlist)
                bot.logger.info {
                    buildString {
                        appendln("[${playlist.tracks.first().sourceManager.javaClass.simpleName}] プレイリスト \"${playlist.name}\" (${playlist.tracks.size}曲, ${playlist.tracks.sumBy { it.duration }.toMilliSecondString()}) をキューに追加しました.")
                        playlist.tracks.forEachIndexed { i, it ->
                            appendln("#${(i + 1).toString().padEnd(playlist.tracks.size.toString().length)}: ${it.info.effectiveTitle} (${it.duration.toMilliSecondString()})")
                        }
                    }
                }
            }

            override fun noMatches() {
                handler.onNoResult()
                bot.logger.warn { "トラック \"$identifier\" は見つかりませんでした." }
            }
            override fun loadFailed(exception: FriendlyException) {
                handler.onFailed(exception)
                bot.logger.error { "トラック \"$identifier\" の読み込み中にエラーが発生しました." }
            }
        })
    }

    fun joinVoiceChannel(channel: VoiceChannel) {
        if (guild.audioManager.isConnected || guild.audioManager.isAttemptingToConnect) {
            guild.audioManager.closeAudioConnection()
        }

        guild.audioManager.openAudioConnection(channel)

        bot.logger.info { "サーバ ${guild.name} で ボイスチャンネル \"${channel.name}\" への接続を開始しました." }
    }

    val voiceChannel: VoiceChannel
        get() {
            if (! guild.audioManager.isConnected && ! guild.audioManager.isAttemptingToConnect) {
                val channel = guild.jda.getVoiceChannelById(config.voiceChannel.general!!)
                joinVoiceChannel(channel)
            }

            return guild.audioManager.connectedChannel ?: guild.audioManager.queuedAudioConnection
        }
}
