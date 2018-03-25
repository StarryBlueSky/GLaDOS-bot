package jp.nephy.glados.component.audio.music

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

interface PlayerLoadResultHandler {
    fun onLoadTrack(track: AudioTrack) {}
    fun onLoadPlaylist(playlist: AudioPlaylist) {}
    fun onNoResult(guildPlayer: GuildPlayer) {}
    fun onFailed(exception: FriendlyException, guildPlayer: GuildPlayer) {}
}
